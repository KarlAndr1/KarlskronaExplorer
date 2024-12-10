package com.team13.karlskronaexplorer.view

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class View {
	Home,
	Find,
	Post
}

@Composable
fun Nav(currentActivity: ComponentActivity, view: View) {
	NavigationBar {
		NavigationBarItem(
			selected = view == View.Home,
			onClick = {
				// https://stackoverflow.com/questions/3591465/on-android-how-do-you-switch-activities-programmatically
				if(view != View.Home) currentActivity.startActivity(Intent(currentActivity, MainActivity::class.java))
			},
			icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }
		)
		NavigationBarItem(
			selected = view == View.Find,
			onClick = {
				if(view != View.Find) currentActivity.startActivity(Intent(currentActivity, FindActivity::class.java))
			},
			icon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Active") }
		)
		NavigationBarItem(
			selected = view == View.Post,
			onClick = {
				if(view != View.Post) currentActivity.startActivity(Intent(currentActivity, PostActivity::class.java))
			},
			icon = { Icon(imageVector = Icons.Default.Share, contentDescription = "Post Location") }
		)
	}
}

@Composable
fun MainTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	MaterialTheme(
		colorScheme = if(darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(
			LocalContext.current),
		content = content
	)
}