package com.team13.karlskronaexplorer.data

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import com.team13.karlskronaexplorer.view.debugSetFilesDirPath
import com.team13.karlskronaexplorer.view.getFilesDirPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private val mockPost = Post(
    0,
    Bitmap.createBitmap(13, 17, Bitmap.Config.RGB_565),
    Position(12.5, 24.25)
)

@RunWith(AndroidJUnit4::class)
class PersistentStorageTest {


    private suspend fun clearStorage() {
        withContext(Dispatchers.IO) {
            val appDir = File(getFilesDirPath())
            for (f in appDir.listFiles()!!) {
                f.delete()
            }
        }
    }

    private suspend fun setup() {
        debugSetFilesDirPath(InstrumentationRegistry.getInstrumentation().targetContext.filesDir.path)
        clearStorage()
    }

    @Test
    fun saveAndLoadActivePost() = runTest {
        setup()
        saveActivePost(mockPost)
        val loadedPost = loadActivePost()!!

        assert(loadedPost.getPosition().getLatitude() == mockPost.getPosition().getLatitude())
        assert(loadedPost.getPosition().getLongitude() == mockPost.getPosition().getLongitude())
        assert(loadedPost.getImage().width == mockPost.getImage().width)
        assert(loadedPost.getImage().height == mockPost.getImage().height)
    }

    @Test
    fun loadActivePostNone() = runTest {
        setup()
        val loadedPost = loadActivePost()
        assert(loadedPost == null)
    }

    @Test
    fun unsetActivePost() = runTest {
        setup()
        saveActivePost(mockPost)

        saveActivePost(null)

        val post = loadActivePost()
        assert(post == null)
    }
}