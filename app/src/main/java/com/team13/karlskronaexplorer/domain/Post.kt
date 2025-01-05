package com.team13.karlskronaexplorer.domain

import android.graphics.Bitmap
import org.json.JSONObject

class Post(private val id: Int, private val image: Bitmap, private val position: Position) {
	fun getImage(): Bitmap {
		return image
	}

	fun getPosition(): Position {
		return position
	}

	fun toJSON(): String {
		val json = JSONObject()
		json.put("id", id)
		json.put("latitude", position.getLatitude())
		json.put("longitude", position.getLongitude())

		return json.toString()
	}
}

fun postFromJSON(json: String, image: Bitmap): Post {
	val obj = JSONObject(json)
	return Post(
		obj.getInt("id"),
		image,
		Position(
			obj.getDouble("latitude"),
			obj.getDouble("longitude")
		)
	)
}