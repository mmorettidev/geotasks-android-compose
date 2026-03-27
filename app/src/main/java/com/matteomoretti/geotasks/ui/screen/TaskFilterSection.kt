package com.matteomoretti.geotasks.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.matteomoretti.geotasks.R
import com.matteomoretti.geotasks.ui.viewmodel.TaskFilter
import com.matteomoretti.geotasks.ui.theme.GeoTasksTheme

@Composable
fun TaskFilterSection(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    val allText = stringResource(R.string.filter_all)
    val pendingText = stringResource(R.string.filter_pending)
    val completedText = stringResource(R.string.filter_completed)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { onFilterSelected(TaskFilter.ALL) }) {
            Text(
                if (selectedFilter == TaskFilter.ALL) {
                    "$allText ✓"
                } else {
                    allText
                }
            )
        }

        Button(onClick = { onFilterSelected(TaskFilter.PENDING) }) {
            Text(
                if (selectedFilter == TaskFilter.PENDING) {
                    "$pendingText ✓"
                } else {
                    pendingText
                }
            )
        }

        Button(onClick = { onFilterSelected(TaskFilter.COMPLETED) }) {
            Text(
                if (selectedFilter == TaskFilter.COMPLETED) {
                    "$completedText ✓"
                } else {
                    completedText
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskFilterSectionPreview() {
    GeoTasksTheme {
        TaskFilterSection(
            selectedFilter = TaskFilter.PENDING,
            onFilterSelected = {}
        )
    }
}