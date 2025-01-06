package com.team13.karlskronaexplorer.view

import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.team13.karlskronaexplorer.domain.Filter
import com.team13.karlskronaexplorer.domain.Position
import com.team13.karlskronaexplorer.domain.Post
import com.team13.karlskronaexplorer.domain.PostFetcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val mockPost = Post(
    0,
    Bitmap.createBitmap(13, 17, Bitmap.Config.RGB_565),
    Position(12.5, 24.25)
)

class MockPostFetcher : PostFetcher(Filter.New) {
    var loadedPosts = 0;

    var atEnd = false

    override suspend fun getPost(): Post? {
        if(atEnd) return null
        loadedPosts++;

        return mockPost
    }

    fun makeNextEnd() {
        atEnd = true
    }
}

@RunWith(AndroidJUnit4::class)
class GalleryViewUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setup(setActivePost: (Post) -> Unit = { _ -> }): MockPostFetcher {
        val fetcher = MockPostFetcher()
        composeTestRule.setContent {
            Gallery(fetcher, setActivePost)
        }

        return fetcher
    }

    private fun SelectPostSenario() {
        composeTestRule.onAllNodesWithTag("GalleryImage")[0].performClick()
    }

    @Test
    fun testClickPost() {
        setup()

        SelectPostSenario()
        composeTestRule.onNodeWithTag("GallerySelectPostDialog").assertExists()
    }

    @Test
    fun testClickSelect() {
        var selectedPost: Post? = null
        setup({ x -> selectedPost = x })

        SelectPostSenario()
        composeTestRule.onNodeWithTag("GallerySelectPostButton").performClick()

        assert(selectedPost == mockPost)
    }

    @Test
    fun testClickCancel() {
        setup()
        SelectPostSenario()
        composeTestRule.onNodeWithTag("GalleryCancelSelectButton").performClick()
        composeTestRule.onNodeWithTag("GallerySelectPostDialog").assertDoesNotExist()
    }

    @Test
    fun testCheckLoadedPosts() {
        val fetcher = setup()

        composeTestRule.waitForIdle()
        assert(fetcher.loadedPosts > 50)
        assert(fetcher.loadedPosts < 150)
    }

}