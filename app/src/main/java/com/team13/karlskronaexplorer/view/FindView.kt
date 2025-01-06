package com.team13.karlskronaexplorer.view

import android.animation.ValueAnimator
import android.util.Log
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.mapbox.common.location.LocationError
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.withCircleColor
import com.mapbox.maps.extension.style.expressions.dsl.generated.pitch
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import kotlin.random.Random

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
fun FindView(innerPadding: PaddingValues, activePost: Post, unselect: ()->Unit) {
	val mapViewportState = rememberMapViewportState()
	var circleColor by remember { mutableStateOf(Color.Red) }
	var locationFound by remember { mutableStateOf(false)}
	val rand = Random(seed = activePost.getRandomSeed())
	val randomLoc1 = rand.nextDouble(0.0001,0.0003)
	val randomLoc2 = rand.nextDouble(0.0001,0.0003)
	if(locationFound) {
		Dialog(
			onDismissRequest = {}
		) {
			Card(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
				Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
					val postImage = activePost.getImage()
					Text("Congratulation, you found the location!")
					Image(
						postImage.asImageBitmap(),
						"Image",
						modifier = Modifier
							.fillMaxWidth()
							.aspectRatio(postImage.width.toFloat() / postImage.height)
							.clip(RoundedCornerShape(8.dp))
						,
						alignment = Alignment.TopCenter
					)
					Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
						Button(onClick = { unselect()}) {
							Text("Confirm")
							Icon(imageVector = Icons.Default.Close, contentDescription = "Confirm")
						}
					}
				}
			}
		}
	}

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
						activePost.getPosition().getLongitude() + randomLoc1,
						activePost.getPosition().getLatitude() + randomLoc2
					)
				)

				.withCircleRadius(150.0)
				.withCircleOpacity(0.3)
				.withCircleColor(circleColor)
				.withCircleStrokeWidth(2.0)
				.withDraggable(false)

			circleAnnotationManager.deleteAll()
			circleAnnotationManager.create(circleAnnotationOptions)

			mapView.location.getLocationProvider()?.registerLocationConsumer(locConsumer({position ->
					var dist = position.distanceTo(activePost.getPosition())
					if (dist < 10) {
						locationFound = true
					}
					else if (dist < 150) {
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

