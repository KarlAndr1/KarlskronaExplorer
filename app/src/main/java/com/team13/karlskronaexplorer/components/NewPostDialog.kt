package com.team13.karlskronaexplorer.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.InputStream

@Composable
fun NewPostDialog(showDialog: Boolean, imageUri: Uri?,location: Location?, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val (bitmap, setBitmap) = remember { mutableStateOf<Bitmap?>(null) }
    val imageLocation = "Location: ${location?.latitude ?: "N/A"}, ${location?.longitude ?: "N/A"}"
    val toast = Toast.makeText( context, "Your new post was successfully created", Toast.LENGTH_SHORT)
    val (isLoading, setIsLoading) = remember { mutableStateOf(false) }

    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val rawBitmap = BitmapFactory.decodeStream(inputStream)
            val correctedBitmap = rawBitmap?.let { correctImageRotation(context, uri, it) }
            setBitmap(correctedBitmap)
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(3000)
            setIsLoading(false)
            toast.show()
            onDismiss()
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp, max = screenHeight * 0.8f)
                    .padding(10.dp)
                    .testTag("NewPostDialog")
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier.padding(10.dp).clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Text("No image captured yet.")
                    }
                    Text(
                        text = imageLocation,
                        fontSize = 16.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = {
                                setIsLoading(true)
                                      },
                            modifier = Modifier.padding(10.dp).width(110.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            )
                        ) {
                            if(!isLoading){
                                Text(text = "Post")
                            }else{
                                CircularProgressIndicator(
                                    modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier.padding(10.dp).width(110.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Discard")
                        }
                    }
                }
            }
        }
    }
}

fun correctImageRotation(context: android.content.Context, uri: Uri, bitmap: Bitmap): Bitmap {
    val inputStream = context.contentResolver.openInputStream(uri)
    val exif = ExifInterface(inputStream!!)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    val rotation = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    val matrix = android.graphics.Matrix().apply {
        postRotate(rotation)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}