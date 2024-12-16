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
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
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
		CameraScreen(cameraExecutor, modifier =Modifier.padding(innerPadding))
	}
}


@Composable
private fun CameraScreen(cameraExecutor: ExecutorService, modifier: Modifier){
	val context = LocalContext.current
	val lifecycleOwner = LocalContext.current as LifecycleOwner
	val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
	val flashMode = remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

	val requestPermissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (!isGranted) {
			// Handle permission denial
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

	Box(modifier = Modifier.fillMaxSize()) {
		if (hasCameraPermission.value) {
			AndroidView(factory = { ctx ->
				val cameraProvider = cameraProviderFuture.get()

				val previewView = androidx.camera.view.PreviewView(ctx)
				val preview = androidx.camera.core.Preview.Builder().build()
				imageCapture.value = ImageCapture.Builder()
					.setFlashMode(flashMode.value)
					.build()
				imageCapture.value = ImageCapture.Builder().build()

				val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

				cameraProvider.unbindAll()
				cameraProvider.bindToLifecycle(
					lifecycleOwner,
					cameraSelector,
					preview,
					imageCapture.value
				)

				preview.setSurfaceProvider(previewView.surfaceProvider)
				previewView
			}, modifier = Modifier.fillMaxSize())
			Column {
				FlashToggleButton(flashMode.value)  { newFlashMode ->
					flashMode.value = newFlashMode
				}
				TakePictureButton(imageCapture.value, cameraExecutor)
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
	val buttonColor = Color(0x66FFFFFF)

	Box(modifier = Modifier.fillMaxSize()) {
		Button(
			colors = ButtonDefaults.elevatedButtonColors(
				containerColor = buttonColor,
				contentColor =  buttonColor,
			),
			onClick = {
				val photoFile = File(
					outputDirectory,
					SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
						.format(System.currentTimeMillis()) + ".jpg"
				)

				val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

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
		){

		}
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
