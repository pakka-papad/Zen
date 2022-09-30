package tech.zemn.mobile.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.zemn.mobile.data.music.Album

@Composable
fun Albums(
    paddingValues: PaddingValues,
    albums: List<Album>,
    gridState: LazyGridState,
    onAlbumClicked: (String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        state = gridState,
        columns = GridCells.Fixed(2),
    ){
        items(albums){ album ->
            AlbumCard(
                album = album,
                onAlbumClicked = onAlbumClicked
            )
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    onAlbumClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    onAlbumClicked(album.name)
                },
            ),
    ) {
        if (album.albumArt != null) {
            Image(
                bitmap = album.albumArt.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = album.name,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}