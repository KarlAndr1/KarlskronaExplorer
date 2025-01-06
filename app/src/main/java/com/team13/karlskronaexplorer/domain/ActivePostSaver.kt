package com.team13.karlskronaexplorer.domain

class ActivePostSaver {
    suspend fun saveActivePost(post: Post?) {
        com.team13.karlskronaexplorer.data.saveActivePost(post)
    }

    suspend fun loadActivePost(): Post? {
        return com.team13.karlskronaexplorer.data.loadActivePost()
    }
}