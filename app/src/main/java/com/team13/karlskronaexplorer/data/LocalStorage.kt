package com.team13.karlskronaexplorer.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.team13.karlskronaexplorer.domain.Post
import com.team13.karlskronaexplorer.domain.postFromJSON
import com.team13.karlskronaexplorer.view.getFilesDirPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun encodePost(post: Post, stream: OutputStream) {
    stream.write(post.toJSON().toByteArray(Charsets.UTF_8))
    stream.write(0)

    post.getImage().compress(Bitmap.CompressFormat.JPEG, 80, stream)
}

fun decodePost(stream: InputStream): Post {
    val header = mutableListOf<Byte>()
    while(true) {
        val byte = stream.read()
        if(byte == 0) break
        if(byte == -1) throw Exception("Unexpected EOF when decoding post header")

        header.add(byte.toByte())
    }

    val image = BitmapFactory.decodeStream(stream)

    return postFromJSON(
        header.toByteArray().toString(Charsets.UTF_8),
        image
    )
}

suspend fun loadActivePost(): Post? {
    return withContext(Dispatchers.IO) {
        val postFile = File(getFilesDirPath(), "active_post");
        if(postFile.isFile && postFile.length() != 0L) {
            val fstream = postFile.inputStream()
            val post = decodePost(fstream)
            fstream.close()
            Log.d("LocalStorage", "LOADED POST:" + post.toJSON())
            post
        } else {
            null
        }
    }
}

suspend fun saveActivePost(post: Post) {
    return withContext(Dispatchers.IO) {
        val fstream = File(getFilesDirPath(), "active_post").outputStream()
        encodePost(post, fstream)
        fstream.close()
    }
}