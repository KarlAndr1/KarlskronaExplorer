package com.team13.karlskronaexplorer.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.team13.karlskronaexplorer.domain.Filter
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import com.team13.karlskronaexplorer.view.MainActivity
import com.team13.karlskronaexplorer.view.getFilesDirPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

// https://stackoverflow.com/questions/5528850/how-do-you-connect-localhost-in-the-android-emulator
const val API_ENDPOINT = "http://10.0.2.2:4000"
//const val API_ENDPOINT = "http://localhost:4000"

private val cachedToken: String? = null
private suspend fun getToken(): String {
	if(cachedToken != null) return cachedToken

	return withContext(Dispatchers.IO) {
		val tokenFile = File(getFilesDirPath(), "token")
		if (tokenFile.isFile && tokenFile.length() != 0L) {
			tokenFile.readBytes().toString(Charsets.UTF_8)
		} else {
			val token = postJSON("$API_ENDPOINT/new-token", useToken = false).getString("token")
			tokenFile.writeBytes(token.toByteArray(Charsets.UTF_8))
			token
		}
	}
}

private suspend fun postJSON(url: String, json: JSONObject? = null, useToken: Boolean = true): JSONObject {
	return withContext(Dispatchers.IO) {
		val connection = URL(url).openConnection() as HttpURLConnection
		connection.requestMethod = "POST"

		if(json != null) connection.doOutput = true
		connection.doInput = true

		if(useToken) {
			connection.setRequestProperty("Authorization", getToken())
		}

		connection.connect()
		if(json != null) {
			connection.outputStream.write(json.toString().toByteArray(Charsets.UTF_8))
		}

		val responseStr = connection.inputStream.readBytes().toString(Charsets.UTF_8)

		connection.disconnect()

		JSONObject(responseStr)
	}
}

private suspend fun fetchJSON(url: String, useToken: Boolean = true): JSONObject {
	return withContext(Dispatchers.IO) {
		val connection = URL(url).openConnection() as HttpURLConnection
		connection.requestMethod = "GET"
		connection.doInput = true

		if(useToken) {
			val token = getToken()
			connection.setRequestProperty("Authorization", token)
		}

		connection.connect()
		val jsonStr = connection.inputStream.readBytes().toString(Charsets.UTF_8)
		connection.disconnect()

		JSONObject(jsonStr)
	}
}

private suspend fun fetchImage(url: String): Bitmap {
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
		val connection = URL("$API_ENDPOINT/new-post").openConnection() as HttpURLConnection
		connection.requestMethod = "POST"
		connection.doOutput = true

		connection.setRequestProperty("Authorization", getToken())

		connection.connect()

		connection.outputStream.write("${coordinates.getLatitude()},${coordinates.getLongitude()}".encodeToByteArray())
		connection.outputStream.write(0)
		image.compress(Bitmap.CompressFormat.JPEG, 80, connection.outputStream)
		connection.outputStream.close()
		connection.inputStream.close()

		connection.disconnect()
	}
}

suspend fun markPostFound(postId: Int) {
	withContext(Dispatchers.IO) {
		postJSON("$API_ENDPOINT/post-found/$postId")
	}
}