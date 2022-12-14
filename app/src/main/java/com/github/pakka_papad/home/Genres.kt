package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.components.more_options.GenreOptions
import com.github.pakka_papad.components.more_options.OptionsAlertDialog
import com.github.pakka_papad.data.music.GenreWithSongCount

@Composable
fun Genres(
    genresWithSongCount: List<GenreWithSongCount>,
    listState: LazyListState,
    onGenreClicked: (GenreWithSongCount) -> Unit,
) {
    if (genresWithSongCount.isEmpty()) {
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
    ) {
        items(
            items = genresWithSongCount,
            key = { it.genreName }
        ) {
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
    options: List<GenreOptions> = listOf(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { onGenreClicked(genreWithSongCount) })
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = genreWithSongCount.genreName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${genreWithSongCount.count} ${if (genreWithSongCount.count == 1) "song" else "songs"}",
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (options.isNotEmpty()) {
            var optionsVisible by remember { mutableStateOf(false) }
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(
                        onClick = {
                            optionsVisible = true
                        },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 20.dp
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
            if (optionsVisible) {
                OptionsAlertDialog(
                    options = options,
                    title = genreWithSongCount.genreName,
                    onDismissRequest = { optionsVisible = false }
                )
            }
        }
    }
}
