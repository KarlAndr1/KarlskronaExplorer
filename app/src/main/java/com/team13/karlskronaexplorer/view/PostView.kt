package com.team13.karlskronaexplorer.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PostView(innerPadding: PaddingValues) {
	Text("Make Post: ...", modifier = Modifier.padding(innerPadding))
}