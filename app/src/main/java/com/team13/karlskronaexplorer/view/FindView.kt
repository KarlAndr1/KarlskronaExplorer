package com.team13.karlskronaexplorer.view

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.withCircleColor
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.team13.karlskronaexplorer.domain.Post
import kotlinx.coroutines.delay

@Composable
fun FindView(innerPadding: PaddingValues, activePost: Post?) {
	val mapViewportState = rememberMapViewportState()
	var circleColor = remember { mutableStateOf(Color.Red) }

	MapboxMap(
		Modifier.fillMaxSize(),
		mapViewportState = mapViewportState,
	) {
		MapEffect(Unit) { mapView ->
			mapView.location.updateSettings {
				locationPuck = createDefault2DPuck(withBearing = true)
				enabled = true
				puckBearing = PuckBearing.COURSE
				puckBearingEnabled = true
			}
			if(activePost != null) {
			mapViewportState.transitionToFollowPuckState()
			val annotationApi = mapView.annotations
			val circleAnnotationManager = annotationApi.createCircleAnnotationManager()
			val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
				.withPoint(
					Point.fromLngLat(
						activePost.getPosition().getLongitude(),
						activePost.getPosition().getLatitude()
					)
				)
				.withCircleRadius(80.0)
				.withCircleColor(circleColor.value)
				.withCircleStrokeWidth(2.0)
				.withCircleStrokeColor(0x55F10000)
				.withDraggable(false)

			circleAnnotationManager.deleteAll()
			circleAnnotationManager.create(circleAnnotationOptions)
			}
		}
		LaunchedEffect(Unit) {
			var i = 0
			while (true) {
				circleColor.value = Color(
					red = 255 - i,
					green = 0 + i,
					blue = 0
				)
				i++
				// Circle Color should update from here if possible
				delay(1000)
			}
		}
	}
}
