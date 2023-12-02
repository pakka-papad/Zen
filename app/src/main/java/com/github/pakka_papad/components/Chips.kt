package com.github.pakka_papad.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SortType(val reversible: Boolean = false) {
    Title(true),
    Album(true),
    Artist(true),
    Length(true),
    Liked(false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortChip(
    sortOptions: List<SortType>,
    onSortSelected: (sortType: SortType, reversed: Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuPageNumber by remember { mutableStateOf(0) }
    var selectedType by remember { mutableStateOf(SortType.Title) }
    AssistChip(
        onClick = { menuPageNumber = 1 },
        label = {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier,
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = "down arrow",
                modifier = Modifier.size(18.dp)
            )
        }
    )
    DropdownMenu(
        expanded = menuPageNumber != 0,
        onDismissRequest = {
            menuPageNumber = 0
        }
    ) {
        when (menuPageNumber) {
            1 -> {
                sortOptions.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it.name
                            )
                        },
                        onClick = {
                            selectedType = it
                            if (it.reversible) {
                                menuPageNumber = 2
                            } else {
                                onSortSelected(selectedType, null)
                                menuPageNumber = 0
                            }
                        },
                        trailingIcon = {
                            if (it.reversible) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowRight,
                                    contentDescription = "right arrow"
                                )
                            }
                        }
                    )
                }
            }
            2 -> {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Ascending"
                        )
                    },
                    onClick = {
                        onSortSelected(selectedType, false)
                        menuPageNumber = 0
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Descending"
                        )
                    },
                    onClick = {
                        onSortSelected(selectedType, true)
                        menuPageNumber = 0
                    }
                )
            }
        }
    }
}