package com.github.pakka_papad.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.music.Composer

@Composable
private fun ResultCard(
    onSeeAllClicked: () -> Unit,
    title: String,
    result: @Composable () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "see-all-arrow",
            modifier = Modifier
                .size(48.dp)
                .rotate(180f)
                .clickable(
                    onClick = onSeeAllClicked,
                    indication = rememberRipple(false, 24.dp),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(9.dp)
        )
    }
    result()
}

@Composable
fun SongResult(
    onSeeAllPressed: () -> Unit,
    onSongClicked: (Song) -> Unit,
    songs: List<Song>,
) {
    if (songs.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Songs"
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(
                items = songs,
                key = { it.location }
            ) { song ->
                SongCardV3(song = song) {
                    onSongClicked(song)
                }
            }
        }
    }
}

@Composable
fun AlbumResult(
    onSeeAllPressed: () -> Unit,
    onAlbumClicked: (Album) -> Unit,
    albums: List<Album>,
) {
    if (albums.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Albums"
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(
                items = albums,
                key = { it.name }
            ) { album ->
                AlbumCardV2(album) {
                    onAlbumClicked(album)
                }
            }
        }
    }
}

@Composable
fun ArtistResult(
    onSeeAllPressed: () -> Unit,
    onArtistClicked: (Artist) -> Unit,
    artists: List<Artist>,
) {
    if (artists.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Artists"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = artists,
                key = { it.name }
            ) {
                TextCard(text = it.name) {
                    onArtistClicked(it)
                }
            }
        }
    }
}

@Composable
fun AlbumArtistResult(
    onSeeAllPressed: () -> Unit,
    onAlbumArtistClicked: (AlbumArtist) -> Unit,
    albumArtists: List<AlbumArtist>,
) {
    if (albumArtists.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Album artists"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = albumArtists,
                key = { it.name }
            ) {
                TextCard(text = it.name) {
                    onAlbumArtistClicked(it)
                }
            }
        }
    }
}

@Composable
fun ComposerResult(
    onSeeAllPressed: () -> Unit,
    onComposerClicked: (Composer) -> Unit,
    composers: List<Composer>,
) {
    if (composers.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Composers"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = composers,
                key = { it.name }
            ) {
                TextCard(text = it.name) {
                    onComposerClicked(it)
                }
            }
        }
    }
}

@Composable
fun LyricistResult(
    onSeeAllPressed: () -> Unit,
    onLyricistClicked: (Lyricist) -> Unit,
    lyricists: List<Lyricist>,
) {
    if (lyricists.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Lyricists"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = lyricists,
                key = { it.name }
            ) {
                TextCard(text = it.name) {
                    onLyricistClicked(it)
                }
            }
        }
    }
}

@Composable
fun GenreResult(
    onSeeAllPressed: () -> Unit,
    onGenreClicked: (Genre) -> Unit,
    genres: List<Genre>,
) {
    if (genres.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Genres"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = genres,
                key = { it.genre }
            ) {
                TextCard(text = it.genre) {
                    onGenreClicked(it)
                }
            }
        }
    }
}

@Composable
fun PlaylistResult(
    onSeeAllPressed: () -> Unit,
    onPlaylistClicked: (Playlist) -> Unit,
    playlists: List<Playlist>,
) {
    if (playlists.isEmpty()) return
    ResultCard(
        onSeeAllClicked = onSeeAllPressed,
        title = "Playlists"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = playlists,
                key = { it.playlistId }
            ) {
                TextCard(text = it.playlistName) {
                    onPlaylistClicked(it)
                }
            }
        }
    }
}

@Composable
private fun SongCardV3(
    song: Song,
    onClick: () -> Unit,
) = Column(
    modifier = Modifier
        .width(160.dp)
        .clickable(onClick = onClick),
    horizontalAlignment = Alignment.Start,
) {
    AsyncImage(
        model = song.artUri,
        contentDescription = "song-album-art",
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    Text(
        text = song.title,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = song.artist,
        maxLines = 1,
        style = MaterialTheme.typography.titleSmall,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AlbumCardV2(
    album: Album,
    onClick: () -> Unit,
) = Column(
    modifier = Modifier
        .width(160.dp)
        .clickable(onClick = onClick),
    horizontalAlignment = Alignment.Start,
) {
    AsyncImage(
        model = album.albumArtUri,
        contentDescription = "album-art",
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    Text(
        text = album.name,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis
    )

}

@Composable
private fun TextCard(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(12.dp)
            .widthIn(max = 300.dp),
        style = MaterialTheme.typography.titleLarge,
        overflow = TextOverflow.Ellipsis
    )
}