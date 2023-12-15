package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.components.more_options.OptionsAlertDialog
import com.github.pakka_papad.components.more_options.PersonOptions
import com.github.pakka_papad.data.music.PersonWithSongCount

@Composable
fun Persons(
    personsWithSongCount: List<PersonWithSongCount>?,
    onPersonClicked: (PersonWithSongCount) -> Unit,
    listState: LazyListState,
    selectedPerson: Person,
    onPersonSelect: (Person) -> Unit,
) {
    if (personsWithSongCount == null) return
    if (personsWithSongCount.isEmpty()) {
        FullScreenSadMessage(
            message = stringResource(R.string.no_artists_found),
            paddingValues = WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                .asPaddingValues(),
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                .asPaddingValues(),
        ) {
            item {
                PersonFilter(
                    selectedPerson = selectedPerson,
                    onPersonSelect = onPersonSelect,
                )
            }
            items(
                items = personsWithSongCount,
                key = { it.name }
            ) { person ->
                PersonCard(
                    personWithSongCount = person,
                    onPersonClicked = onPersonClicked,
                )
            }
        }
    }
}

@Composable
fun PersonCard(
    personWithSongCount: PersonWithSongCount,
    onPersonClicked: (PersonWithSongCount) -> Unit,
    options: List<PersonOptions> = listOf()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { onPersonClicked(personWithSongCount) })
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
                text = personWithSongCount.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.song_count,
                    count = personWithSongCount.count,
                    personWithSongCount.count
                ),
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
                contentDescription = stringResource(R.string.more_menu_button),
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
                    title = personWithSongCount.name,
                    onDismissRequest = { optionsVisible = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonFilter(
    selectedPerson: Person,
    onPersonSelect: (Person) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        items(
            items = Person.values(),
        ) { person ->
            FilterChip(
                selected = (person == selectedPerson),
                onClick = { onPersonSelect(person) },
                label = {
                    Text(
                        text = person.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}