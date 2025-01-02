package com.team13.karlskronaexplorer.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import java.util.concurrent.Executors
import com.team13.karlskronaexplorer.data.Post

@Composable
fun FindView(innerPadding: PaddingValues) {
	MapboxMap(
		Modifier.fillMaxSize(),
		mapViewportState = rememberMapViewportState {
			setCameraOptions {
				zoom(11.0)
				center(Point.fromLngLat(15.5867142, 56.1608158))
				pitch(0.0)
				bearing(0.0)
			}
		},
	)
}