package com.team13.karlskronaexplorer.domain

import com.team13.karlskronaexplorer.data.Post
import com.team13.karlskronaexplorer.data.fetchPost

enum class Filter(private val display: String, private val id: String) {
	New("New", "new"),
	Close("Close", "close"),
	Found("Found", "found"),
	MyLocations("My Locations", "mylocations");

	fun getId(): String {
		return id
	}

	fun getDisplayName(): String {
		return name
	}
}

class PostViewContext(private val filter: Filter) {
	private var atId: Int? = null
	private var atEnd: Boolean = false

	suspend fun getPost(): Post? {
		if(atEnd) return null

		val res = fetchPost(filter, atId)
		if(res == null) {
			atEnd = true
			return null
		}

		atId = res.second
		return res.first
	}
}