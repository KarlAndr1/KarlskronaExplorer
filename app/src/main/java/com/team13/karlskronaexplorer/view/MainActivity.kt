package com.team13.karlskronaexplorer.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

enum class View {
    Home,
    Find,
    Post
}

@Composable
fun Nav(view: View, setView: (View) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = view == View.Home,
            onClick = {
                if(view != View.Home) setView(View.Home)
            },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") }
        )
        NavigationBarItem(
            selected = view == View.Find,
            onClick = {
                if(view != View.Find) setView(View.Find)
            },
            icon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Active") }
        )
        NavigationBarItem(
            selected = view == View.Post,
            onClick = {
                if(view != View.Post) setView(View.Post)
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedView by remember { mutableStateOf(View.Home) }
            MainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { Nav(selectedView) { view -> selectedView = view } },
                ) { innerPadding ->
                    when(selectedView) {
                        View.Home -> HomeView(innerPadding)
                        View.Post -> PostView(innerPadding)
                        View.Find -> FindView(innerPadding)
                    }
                }
            }
        }
    }
}