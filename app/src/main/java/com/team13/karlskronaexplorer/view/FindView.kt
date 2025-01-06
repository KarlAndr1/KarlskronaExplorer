package com.team13.karlskronaexplorer.view

import android.animation.ValueAnimator
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mapbox.common.location.LocationError
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.withCircleColor
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post

class locConsumer(private val callback: (Position) -> Unit) : LocationConsumer {
	override fun onBearingUpdated(vararg bearing: Double, options: (ValueAnimator.() -> Unit)?) {
	}

	override fun onError(error: LocationError) {
	}

	override fun onHorizontalAccuracyRadiusUpdated(
		vararg radius: Double,
		options: (ValueAnimator.() -> Unit)?
	) {
	}

	override fun onLocationUpdated(vararg location: Point, options: (ValueAnimator.() -> Unit)?) {
		callback(Position(location[0].latitude(), location[0].longitude()))
	}

	override fun onPuckAccuracyRadiusAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
	}

	override fun onPuckBearingAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
	}

	override fun onPuckLocationAnimatorDefaultOptionsUpdated(options: ValueAnimator.() -> Unit) {
	}

}

@Composable
fun FindView(innerPadding: PaddingValues, activePost: Post) {
	val mapViewportState = rememberMapViewportState()
	var circleColor by remember { mutableStateOf(Color.Red) }

	MapboxMap(
		Modifier.fillMaxSize().padding(innerPadding),
		mapViewportState = mapViewportState,
	) {
		MapEffect(circleColor) { mapView ->
			mapView.location.updateSettings {
				locationPuck = createDefault2DPuck(withBearing = true)
				enabled = true
				puckBearing = PuckBearing.COURSE
				puckBearingEnabled = true
			}
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
				.withCircleRadius(100.00)
				.withCircleOpacity(0.3)
				.withCircleColor(circleColor)
				.withCircleStrokeWidth(2.0)
				.withDraggable(false)

			circleAnnotationManager.deleteAll()
			circleAnnotationManager.create(circleAnnotationOptions)

			mapView.location.getLocationProvider()?.registerLocationConsumer(locConsumer({position ->
					var dist = position.distanceTo(activePost.getPosition())
					if (dist < 600) {
						circleColor = Color.Green
					} else {
						circleColor = Color.Red
					}
					Log.d("test", dist.toString())
			}))

		}
	}
	Column(Modifier.fillMaxWidth().padding(0.dp,15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
		Image(
			activePost.getImage().asImageBitmap(),
			"Select Post Image",
			modifier = Modifier
				.fillMaxWidth(0.20f)
				.aspectRatio(1f)
				.clip(RoundedCornerShape(8.dp))
			,
			alignment = Alignment.TopCenter,
			contentScale = ContentScale.Crop
		)
	}
}

