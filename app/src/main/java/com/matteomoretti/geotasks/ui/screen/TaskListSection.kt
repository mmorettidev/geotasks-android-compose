package com.matteomoretti.geotasks.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.matteomoretti.geotasks.R
import com.matteomoretti.geotasks.data.model.Task

@Composable
fun TaskListSection(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onRenameClick: (Long) -> Unit
) {
    if (tasks.isEmpty()) {
        Text(
            text = stringResource(R.string.no_tasks_found),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.empty_state_message),
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + shrinkVertically()
                ) {
                    TaskCard(
                        task = task,
                        onTaskClick = { onTaskClick(task.id) },
                        onDeleteClick = { onDeleteClick(task.id) },
                        onRenameClick = { onRenameClick(task.id) }
                    )
                }
            }
        }
    }
}