package com.team13.karlskronaexplorer.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class Post(private val id: Int, private val image: Bitmap) {
	fun getImage(): Bitmap {
		return image;
	}
}

public suspend fun fetchPost(id: Int): Post {
	return withContext(Dispatchers.IO) {
		val netStream = URL("https://picsum.photos/200").openStream()
		val image = BitmapFactory.decodeStream(netStream)
		netStream.close()
		Post(
			id,
			image
		);
	}
}