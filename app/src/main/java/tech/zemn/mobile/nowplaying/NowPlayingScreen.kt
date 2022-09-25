package tech.zemn.mobile.nowplaying

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.zemn.mobile.data.music.Song

@Composable
fun NowPlayingScreen(
    paddingValues: PaddingValues,
    song: Song,
    onPausePlayPressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
){
  Box(
    modifier = Modifier.fillMaxSize()
  ) {

  }
}