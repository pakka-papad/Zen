package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.data.music.GenreWithSongCount

@Composable
fun Genres(
    genresWithSongCount: List<GenreWithSongCount>,
    listState: LazyListState,
    onGenreClicked: (GenreWithSongCount) -> Unit,
){
    if (genresWithSongCount.isEmpty()){
        FullScreenSadMessage(
            message = "Nothing found"
        )
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ){
        items(
            items = genresWithSongCount,
            key = { it.genreName }
        ){
            GenreCard(
                genreWithSongCount = it,
                onGenreClicked = onGenreClicked
            )
        }
    }
}

@Composable
fun GenreCard(
    genreWithSongCount: GenreWithSongCount,
    onGenreClicked: (GenreWithSongCount) -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = {
                    onGenreClicked(genreWithSongCount)
                }
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = genreWithSongCount.genreName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${genreWithSongCount.count} ${if (genreWithSongCount.count == 1) "song" else "songs"}",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            onClick = {

                            },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 20.dp
                            ),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(8.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(0.8.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}
