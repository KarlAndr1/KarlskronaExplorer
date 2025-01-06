package com.team13.karlskronaexplorer.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.team13.karlskronaexplorer.domain.Filter
import com.team13.karlskronaexplorer.domain.Post
import com.team13.karlskronaexplorer.domain.PostFetcher
import java.io.IOException

@Composable
fun HomeView(innerPadding: PaddingValues, setActivePost: (Post) -> Unit) {
	var selectedFilter: Filter by remember { mutableStateOf(Filter.New) }
	val postFetcher = PostFetcher(selectedFilter)

	Column(Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
		FilterButtons(selectedFilter, { x -> selectedFilter = x })
		Gallery(postFetcher, setActivePost)
	}
}

@Composable
private fun FilterButtons(selected: Filter?, setSelected: (Filter) -> Unit) {
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
		Filter.entries.forEach { filter ->
			FilterChip(
				selected = filter == selected,
				onClick = { setSelected(filter) },
				label = { Text(filter.getDisplayName()) }
			)
		}
	}
}

@Composable
fun Gallery(fetchCtx: PostFetcher, setActivePost: (Post) -> Unit) {
	// https://stackoverflow.com/questions/68919900/screen-width-and-height-in-jetpack-compose
	val screenWidth = LocalConfiguration.current.screenWidthDp
	val screenHeight = LocalConfiguration.current.screenHeightDp

	val posts = remember(fetchCtx) { mutableStateListOf<Post>() }
	var scroll by remember(fetchCtx) { mutableFloatStateOf(0f) }
	var selectedPost by remember(fetchCtx) { mutableStateOf<Post?>(null) }
	var fetchError by remember(fetchCtx) { mutableStateOf(false) }

	val bufferPosts = 42; // Number of extra posts past the end that should be loaded
	val spacing = 10.dp; // Spacing between each grid item
	val itemSize = 80.dp; // Size of each grid item (e.g the thumbnail)
	val rows = (screenHeight.dp / (spacing + itemSize)).toInt() + 1;
	val itemsPerRow = (screenWidth.dp / (spacing + itemSize)).toInt();

	val scrollOffset = scroll % (itemSize + spacing).value
	val scrolledRows = (scroll / (itemSize + spacing).value).toInt()

	// Given how many posts are currently loaded, when should the scrolling stop
	// so that the user can't scroll too deep into the unloaded section
	val maxScroll = ((posts.size + bufferPosts) / itemsPerRow - rows - 1) * (itemSize + spacing).value

	val fetchErrorToast = Toast.makeText(LocalContext.current, "Something went wrong whilst fetching posts", Toast.LENGTH_SHORT)

	val lastVisiblePost = (scrolledRows + rows) * itemsPerRow
	if(posts.size < lastVisiblePost + bufferPosts && !fetchError) {
		LaunchedEffect(fetchCtx, posts.size) {
			try {
				val post = fetchCtx.getPost()
				if(post != null)  posts.add(post)
			} catch (_: IOException) {
				fetchErrorToast.show()
				fetchError = true
			}
		}
	}

	if(fetchError && posts.size == 0) {
		Text("Unable to fetch any posts; Check your network connection")
		return
	}

	//Text("Scroll $scroll, maxScroll $maxScroll, loaded ${posts.size}")
	Box(Modifier.fillMaxWidth().scrollable(
		ScrollableState { x: Float ->
			val current = scroll
			scroll -= x
			if(scroll > maxScroll) scroll = maxScroll
			if (scroll < 0) scroll = 0f

			scroll - current // Delta
		},
		orientation = Orientation.Vertical,
		enabled = true,
	).clipToBounds()) {
		if(selectedPost != null) {
			Dialog(
				onDismissRequest = { selectedPost = null },
			) {
				Card(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).testTag("GallerySelectPostDialog")) {
					Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
						val postImage = selectedPost!!.getImage()
						Image(
							postImage.asImageBitmap(),
							"Select Post Image",
							modifier = Modifier
								.fillMaxWidth()
								.aspectRatio(postImage.width.toFloat() / postImage.height)
								.clip(RoundedCornerShape(8.dp))
							,
							alignment = Alignment.TopCenter
						)
						Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
							Button(onClick = { setActivePost(selectedPost!!) }, modifier = Modifier.testTag("GallerySelectPostButton")) {
								Text("Select")
								Icon(imageVector = Icons.Default.Check, contentDescription = "Select Post")
							}
							Button(onClick = { selectedPost = null }, modifier = Modifier.testTag("GalleryCancelSelectButton")) {
								Icon(imageVector = Icons.Default.Close, contentDescription = "Close Post View")
							}
						}
					}
				}
			}
		}
		Column(Modifier.fillMaxSize().wrapContentHeight(align = Alignment.Top, unbounded = true).offset(0.dp, -scrollOffset.dp), verticalArrangement = Arrangement.spacedBy(spacing), horizontalAlignment = Alignment.CenterHorizontally) {
			repeat(rows) { row ->
				Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
					repeat(itemsPerRow) { item ->
						val index = item + (scrolledRows + row) * itemsPerRow
						if(index < posts.size) {
							Image(
								posts[index].getImage().asImageBitmap(),
								"Post Thumbnail",
								modifier = Modifier.requiredSize(itemSize).testTag("GalleryImage").clickable { selectedPost = posts[index] },
								contentScale = ContentScale.Crop
							)
						} else {
							Box(Modifier.requiredSize(itemSize)) {
								//Text("$index")
							}
						}
					}
				}
			}
		}
	}
}