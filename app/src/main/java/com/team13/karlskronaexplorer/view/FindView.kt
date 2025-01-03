package com.team13.karlskronaexplorer.view

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.bindgen.Expected
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.team13.karlskronaexplorer.domain.Post
import java.util.concurrent.Executors

@Composable
fun ViewAnnotationContent() {
	Text(
		text = "Hello world",
		modifier = Modifier
			.padding(3.dp)
			.width(100.dp)
			.height(60.dp)
			.background(
				Color.Red
			),
		textAlign = TextAlign.Center,
		fontSize = 20.sp
	)
}

@Composable
fun FindView(innerPadding: PaddingValues, activePost: Post?) {
	val mapViewportState = rememberMapViewportState() {
	}
	MapboxMap(
		Modifier.fillMaxSize(),
		mapViewportState = mapViewportState,
		) {
		ViewAnnotation(
			options = viewAnnotationOptions {
				// View annotation is placed at the specific geo coordinate
				geometry(Point.fromLngLat(15.5867142, 56.1608158))
			}
		) {
			// Insert the content of the ViewAnnotation
			ViewAnnotationContent()
		}
		MapEffect(Unit) { mapView ->
			mapView.location.updateSettings {
				locationPuck = createDefault2DPuck(withBearing = true)
				enabled = true
				puckBearing = PuckBearing.COURSE
				puckBearingEnabled = true
			}
			mapViewportState.transitionToFollowPuckState()

		}

	}
}



