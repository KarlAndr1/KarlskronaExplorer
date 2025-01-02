package com.team13.karlskronaexplorer.domain

import android.graphics.Bitmap

class Post(private val id: Int, private val image: Bitmap, private val position: Position) {
	fun getImage(): Bitmap {
		return image
	}

	fun getPosition(): Position {
		return position
	}
}