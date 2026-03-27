package com.matteomoretti.geotasks.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextDecoration
import com.matteomoretti.geotasks.ui.theme.GeoTasksTheme
import com.matteomoretti.geotasks.data.model.Task
import com.matteomoretti.geotasks.util.formatDate

@Composable
fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRenameClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onTaskClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Text(
                text = formatDate(task.createdAt),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = task.locationLabel,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Lat: %.4f, Lng: %.4f".format(task.latitude, task.longitude),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (task.isCompleted) "Completed" else "Pending",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .semantics { contentDescription = "Delete task ${task.title}" }
                        .clickable { onDeleteClick() }
                )

                Text(
                    text = "Rename",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .semantics { contentDescription = "Rename task ${task.title}" }
                        .clickable { onRenameClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskCardPreview() {
    GeoTasksTheme {
        TaskCard(
            task = Task(
                id = 1L,
                title = "Buy cables near office",
                description = "Need HDMI adapter and USB-C cable",
                latitude = 43.7696,
                longitude = 11.2558,
                locationLabel = "Office",
                isCompleted = false,
                createdAt = 1710000000000L
            ),
            onTaskClick = {},
            onDeleteClick = {},
            onRenameClick = {}
        )
    }
}