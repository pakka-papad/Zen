package com.github.pakka_papad.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.github.pakka_papad.Screens

/**
 * Do not change order
 */
enum class SortOptions(
    val text: String,
) {
    Default(text = "Default"),
    TitleASC(text = "Title - Ascending"),
    TitleDSC(text = "Title - Descending"),
    ArtistASC(text = "Artist - Descending"),
    ArtistDSC(text = "Artist - Descending"),
    AlbumASC(text = "Album - Ascending"),
    AlbumDSC(text = "Album - Descending"),
    YearASC(text = "Year - Ascending"),
    YearDSC(text = "Year - Descending"),
    DurationASC(text = "Duration - Ascending"),
    DurationDSC(text = "Duration - Descending"),
    NameASC(text = "Name - Ascending"),
    NameDSC(text = "Name - Descending"),
    SongsCountASC(text = "Songs count - Ascending"),
    SongsCountDSC(text = "Songs count - Descending"),
}

fun Screens.getSortOptions(): List<SortOptions> {
    return when (this) {
        Screens.Songs -> listOf(
            SortOptions.TitleASC,
            SortOptions.TitleDSC,
            SortOptions.AlbumASC,
            SortOptions.AlbumDSC,
            SortOptions.ArtistASC,
            SortOptions.ArtistDSC,
            SortOptions.YearASC,
            SortOptions.YearDSC,
            SortOptions.DurationASC,
            SortOptions.DurationDSC,
        )

        Screens.Albums -> listOf(
            SortOptions.TitleASC,
            SortOptions.TitleDSC,
        )

        Screens.Artists, Screens.Genres, Screens.Playlists -> listOf(
            SortOptions.NameASC,
            SortOptions.NameDSC,
            SortOptions.SongsCountASC,
            SortOptions.SongsCountDSC,
        )

        Screens.Folders -> listOf(
            SortOptions.Default,
            SortOptions.NameASC,
            SortOptions.NameDSC,
        )
    }
}

@Composable
fun SortOptionChooser(
    options: List<SortOptions>,
    selectedOption: Int,
    onOptionSelect: (Int) -> Unit,
    onChooserDismiss: () -> Unit,
) {
    if (options.isEmpty()) return
    AlertDialog(
        onDismissRequest = onChooserDismiss,
        title = {
            Text(
                text = "Sort",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onChooserDismiss,
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = options,
                    key = { option -> option.ordinal }
                ) { option ->
                    SortOptionCard(
                        option = option,
                        isSelected = (selectedOption == option.ordinal),
                        onSelect = { onOptionSelect(option.ordinal) }
                    )
                }
            }
        }
    )
}

@Composable
private fun SortOptionCard(
    option: SortOptions,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Text(
            text = option.text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}