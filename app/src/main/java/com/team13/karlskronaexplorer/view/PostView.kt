package com.team13.karlskronaexplorer.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.team13.karlskronaexplorer.components.FlashToggleButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
@Composable
fun PostView(innerPadding: PaddingValues) {
	val cameraExecutor = Executors.newSingleThreadExecutor()
	Column(modifier = Modifier.padding(innerPadding)) {
		CameraScreen(cameraExecutor, modifier = Modifier.padding(innerPadding))
	}
}

@Composable
private fun CameraScreen(cameraExecutor: ExecutorService, modifier: Modifier) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	val (flashMode, setFlashMode) = remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }
	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
	val cameraProvider = cameraProviderFuture.get()
	val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
	val preview = Preview.Builder().build()

	val requestPermissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (!isGranted) {
			println("Request denied")
		}
	}

	val hasCameraPermission = remember {
		mutableStateOf(
			ContextCompat.checkSelfPermission(
				context,
				Manifest.permission.CAMERA
			) == PackageManager.PERMISSION_GRANTED
		)
	}

	if (!hasCameraPermission.value) {
		LaunchedEffect(Unit) {
			requestPermissionLauncher.launch(Manifest.permission.CAMERA)
		}
	}

	val (imageCapture, setImageCapture) = remember {mutableStateOf(ImageCapture.Builder()
		.setFlashMode(flashMode)
		.build())}

	fun updateFlashMode(imageCapture: ImageCapture, flashMode: Int) {
		imageCapture.setFlashMode(flashMode)
	}

	LaunchedEffect(cameraProvider) {
		cameraProvider.bindToLifecycle(
			lifecycleOwner, cameraSelector, preview, imageCapture
		)
		updateFlashMode(imageCapture, flashMode)
	}



	Box(modifier = Modifier.fillMaxSize()) {
		if (hasCameraPermission.value) {
			AndroidView(factory = { ctx ->
				val previewView = PreviewView(ctx)
				preview.setSurfaceProvider(previewView.surfaceProvider)
				previewView
			}, modifier = Modifier.fillMaxSize())

			Column {
				// Flash toggle button
				FlashToggleButton(flashMode) { newFlashMode ->
					setFlashMode(newFlashMode)
					updateFlashMode(imageCapture, newFlashMode)
				}
				// Take picture button
				TakePictureButton(imageCapture, cameraExecutor)
			}
		} else {
			Text("Camera permission is required")
		}
	}
}


@Composable
fun TakePictureButton(imageCapture: ImageCapture?, cameraExecutor: ExecutorService) {
	val context = LocalContext.current
	val outputDirectory = getOutputDirectory(context)
	val buttonColor = Color.White.copy(alpha = 0.4f)

	Box(modifier = Modifier.fillMaxSize()) {
		Button(
			colors = ButtonDefaults.elevatedButtonColors(
				containerColor = buttonColor,
				contentColor = buttonColor,
			),
			onClick = {
				val photoFile = File(
					outputDirectory,
					SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
						.format(System.currentTimeMillis()) + ".jpg"
				)

				val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

				// Take picture with flash applied based on flashMode state
				imageCapture?.takePicture(
					outputOptions,
					cameraExecutor,
					object : ImageCapture.OnImageSavedCallback {
						override fun onError(exception: ImageCaptureException) {
							println("ERROR:::")
							println(exception)
						}

						override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
							val savedUri = Uri.fromFile(photoFile)
							println("Photo saved: $savedUri")
						}
					}
				)
			},
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(16.dp)
				.size(80.dp),
			shape = CircleShape
		) {}
	}
}

fun getOutputDirectory(context: Context): File {
	val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
		File(it, getApplicationName(context)).apply { mkdirs() }
	}
	return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}

fun getApplicationName(context: Context): String {
	val applicationInfo = context.applicationInfo
	val stringId = applicationInfo.labelRes
	return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
		stringId
	)
}
