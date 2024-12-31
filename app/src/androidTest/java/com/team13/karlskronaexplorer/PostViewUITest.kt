package com.team13.karlskronaexplorer.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostViewUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun setUpTest(){
        composeTestRule.setContent {
            PostView(innerPadding = PaddingValues(0.dp))
        }
    }

    @Test
    fun cameraButtonClick_showsNewPostDialog() {
        setUpTest()

        composeTestRule.onNodeWithTag("CameraButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Post").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("NewPostDialog").assertExists()
    }

    @Test
    fun testDismissNewPostDialog() {
        setUpTest()

        composeTestRule.onNodeWithTag("CameraButton").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Discard").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Discard").performClick()
        composeTestRule.onNodeWithTag("NewPostDialog").assertDoesNotExist()
    }
}
