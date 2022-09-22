package tech.zemn.mobile.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.zemn.mobile.data.music.Song

@Composable
fun AllSongs(
    songs: List<Song>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(songs) { song ->
            SongCard(
                song = song,
                onSongClicked = {

                }
            )
        }
    }
}

@Composable
fun SongCard(
    song: Song,
    onSongClicked: (Song) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .padding(start = 10.dp)
                .clickable(
                    onClick = {
                        onSongClicked(song)
                    }
                )
        ) {
            Text(
                text = song.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = song.artist,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = song.duration.toString(),
                    fontSize = 14.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Outlined.Favorite, contentDescription = null)
            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SongCardPreview() {
//    SongCard(
//        song = Song(
//            title = "Shape of You",
//            metadata = Song.Metadata(
//                artist = "Ed Sheeran",
//                duration = "5 m"
//            ),
//        )
//    ) {
//
//    }
}