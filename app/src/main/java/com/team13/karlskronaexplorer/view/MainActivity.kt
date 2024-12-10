package com.team13.karlskronaexplorer.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class Filter(val message: String) {
    New("New"),
    Close("Close"),
    Found("Found"),
    MyLocations("My Locations")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedFilter: Filter by remember { mutableStateOf(Filter.New) }
            MainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { Nav(this, View.Home) },
                ) { innerPadding ->
                    Column(Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
                        FilterButtons(selectedFilter, { x -> selectedFilter = x })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterButtons(selected: Filter?, setSelected: (Filter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Filter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { setSelected(filter) },
                label = { Text(filter.message) }
            )
        }
    }
}