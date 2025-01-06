// https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/
// https://docs.mapbox.com/android/navigation/guides/device-location/
// https://docs.mapbox.com/android/maps/guides/annotations/annotations/#polygon-annotation


package com.team13.karlskronaexplorer.view

import android.animation.ValueAnimator
import android.util.Log
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

const val DIST_SUCCESS = 10
const val DIST_CLOSER = 100

const val CIRCLE_COLOR_RED = 0xffee4e8b
const val CIRCLE_COLOR_GREEN = 0xff00ee00

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
	var circleColor by remember { mutableLongStateOf(CIRCLE_COLOR_RED) }
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
		MapEffect(Unit) { mapView ->
			mapView.location.updateSettings {
				locationPuck = createDefault2DPuck(withBearing = true)
				enabled = true
				puckBearing = PuckBearing.COURSE
				puckBearingEnabled = true
			}
			mapViewportState.transitionToFollowPuckState()

			mapView.location.getLocationProvider()?.registerLocationConsumer(locConsumer({position ->
				val dist = position.distanceTo(activePost.getPosition()).toInt()
				if (dist < DIST_SUCCESS) {
					locationFound = true
				}
				circleColor = if (dist < DIST_CLOSER) {
					CIRCLE_COLOR_GREEN
				} else {
					CIRCLE_COLOR_RED
				}
				Log.d("test", dist.toString())
			}))
		}
			PolygonAnnotation(
				points = listOf(
					getListOfCirclePoints(
						Position(activePost.getPosition().getLatitude()+randomLoc1,
								activePost.getPosition().getLongitude()+randomLoc2))
				)
			) {
				fillColor = Color(circleColor)
				fillOpacity = 0.4
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

fun getListOfCirclePoints(position: Position): MutableList<Point> {
	val circlePoints = mutableListOf<Point>()
	for (i in 0..360) {
		val r = i.toDouble() / 180 * PI
		circlePoints.add(
			Point.fromLngLat(position.getLongitude() + cos(r)/250,
				position.getLatitude()+ sin(r)/500))
	}
	return circlePoints
}