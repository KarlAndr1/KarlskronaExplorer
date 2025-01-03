package com.team13.karlskronaexplorer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.team13.karlskronaexplorer.domain.Post

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
	val mapViewportState = rememberMapViewportState {
	}
	MapboxMap(
		Modifier.fillMaxSize(),
		mapViewportState = mapViewportState,
		) {
		ViewAnnotation(
			options = viewAnnotationOptions {
				// View annotation is placed at the specific geo coordinate
				if(activePost?.getPosition() != null) {
					geometry(Point.fromLngLat(activePost.getPosition().getLongitude(), activePost.getPosition().getLatitude()))
				} else {
					geometry(Point.fromLngLat(11.11,11.11))
				}

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



