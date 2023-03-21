package com.github.pakka_papad.whatsnew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChangelogUi(
    changelog: Changelog
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp,MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.shapes.extraLarge)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(
            text = "Version ${changelog.versionName}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = changelog.date,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp,MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
                .padding(12.dp)
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                changelog.changes.forEach {change ->
                    Text(
                        text = "• $change",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }
        }
    }
}