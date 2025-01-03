package com.team13.karlskronaexplorer.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.team13.karlskronaexplorer.domain.Filter
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// https://stackoverflow.com/questions/5528850/how-do-you-connect-localhost-in-the-android-emulator
const val API_ENDPOINT = "http://10.0.2.2:4000"
//const val API_ENDPOINT = "http://localhost:4000"

@SuppressLint("NewApi")
suspend fun fetchJSON(url: String): JSONObject {
	return withContext(Dispatchers.IO) {
		val netStream = URL(url).openStream()
		val reader = netStream.bufferedReader()
		val jsonStr = StringBuilder()
		while(true) {
			val line = reader.readLine() ?: break
			jsonStr.append(line)
		}
		netStream.close()
		JSONObject(jsonStr.toString())
	}
}

suspend fun fetchImage(url: String): Bitmap {
	return withContext(Dispatchers.IO) {
		val netStream = URL(url).openStream()
		val image = BitmapFactory.decodeStream(netStream)
		netStream.close()

		image
	}
}

suspend fun fetchPost(filter: Filter, beginAtId: Int? = null): Pair<Post, Int>? {
	return withContext(Dispatchers.IO) {
		val urlIdSuffix = if(beginAtId == null) "" else "/$beginAtId"
		val postUrl = "$API_ENDPOINT/posts/${filter.getId()}$urlIdSuffix"

		val response = fetchJSON(postUrl)

		if(response.length() == 0) {
			null
		} else {
			val id = response.getInt("id")
			val nextId = id - 1
			val image = fetchImage(response.getString("image_ref"))

			val position = Position(response.getDouble("latitude"), response.getDouble("longitude"))

			Pair(Post(id, image, position), nextId)
		}
	}
}

suspend fun makePost(coordinates: Position, image: Bitmap) {
	withContext(Dispatchers.IO) {
		// https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
		val connection = URL("$API_ENDPOINT/new_post").openConnection() as HttpURLConnection
		connection.requestMethod = "POST"
		connection.doOutput = true
		connection.connect()

		connection.outputStream.write("${coordinates.getLatitude()},${coordinates.getLongitude()}".encodeToByteArray())
		connection.outputStream.write(0)
		image.compress(Bitmap.CompressFormat.JPEG, 100, connection.outputStream)

		connection.disconnect()
	}
}