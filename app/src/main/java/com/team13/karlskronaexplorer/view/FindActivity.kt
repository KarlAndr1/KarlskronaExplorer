package com.team13.karlskronaexplorer.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

class FindActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MainTheme {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					bottomBar = { Nav(this, View.Find) }
				) { innerPadding ->
					Text("Active Location: ...", modifier = Modifier.padding(innerPadding))
				}
			}
		}
	}
}