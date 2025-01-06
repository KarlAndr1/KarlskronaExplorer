package com.team13.karlskronaexplorer.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.team13.karlskronaexplorer.data.loadActivePost
import com.team13.karlskronaexplorer.data.saveActivePost
import com.team13.karlskronaexplorer.domain.Post
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class View {
    Home,
    Find,
    Post
}

@Composable
fun Nav(view: View, activePost:Post?, setView: (View) -> Unit) {
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
                if (activePost != null) {
                    if(view != View.Find) setView(View.Find)
                }
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

private var filesDirPath: String? = null
fun getFilesDirPath(): String {
    return filesDirPath!!
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filesDirPath = this.filesDir.path

        enableEdgeToEdge()
        setContent {
            var selectedView by remember { mutableStateOf(View.Home) }
            var activePost by remember { mutableStateOf<Post?>(null) }
            val context = LocalContext.current

            val (hasPermissions, setHasPermissions) = remember { mutableStateOf(false) }
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions[Manifest.permission.CAMERA] == true &&
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                setHasPermissions(granted)
            }

            LaunchedEffect(Unit) {
                val permissionsGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                setHasPermissions(permissionsGranted)

                if (!hasPermissions) {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }

            LaunchedEffect(Unit) { // Run only once; see https://stackoverflow.com/questions/75232544/how-to-make-launchedeffect-run-once-and-never-again
                activePost = loadActivePost()
            }

            fun setActivePost(post: Post?) {
                activePost = post
                selectedView = View.Find
                GlobalScope.launch {
                    saveActivePost(post)
                }
            }

            fun unselectPost() {
                setActivePost(null)
                selectedView = View.Home
            }

            MainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { Nav(selectedView, activePost) { view -> selectedView = view } },
                ) { innerPadding ->
                    when(selectedView) {
                        View.Home -> HomeView(innerPadding, ::setActivePost)
                        View.Post -> PostView(innerPadding)
                        View.Find -> FindView(innerPadding, activePost!!)
                    }
                }
            }
        }
    }
}