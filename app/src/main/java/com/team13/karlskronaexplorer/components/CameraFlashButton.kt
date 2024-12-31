package com.team13.karlskronaexplorer.components

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.team13.karlskronaexplorer.R

@Composable
fun FlashToggleButton(currentFlashMode: Int, modifier: Modifier = Modifier,onFlashModeChange: (Int) -> Unit) {
    val flashModes = listOf(
        ImageCapture.FLASH_MODE_OFF to R.drawable.flash_off,
        ImageCapture.FLASH_MODE_ON to R.drawable.flash_on,
        ImageCapture.FLASH_MODE_AUTO to R.drawable.flash_auto
    )
    val buttonColor = Color(0x66FFFFFF)


    Box(modifier= Modifier.fillMaxWidth().padding(20.dp)){
        Button(
            onClick = {
                val nextFlashModeIndex = (flashModes.indexOfFirst { it.first == currentFlashMode } + 1) % flashModes.size
                onFlashModeChange(flashModes[nextFlashModeIndex].first)
            },
            modifier = Modifier.size(40.dp).align(Alignment.TopEnd).testTag("FlashStateButton"),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = buttonColor,
                contentColor =  buttonColor,
            ),
        ) {
            Image(
                painter = painterResource(id = flashModes.first { it.first == currentFlashMode }.second),
                contentDescription = "Flash Mode",
                contentScale = ContentScale.Crop
            )
        }
    }


}