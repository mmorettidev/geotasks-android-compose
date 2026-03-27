package com.matteomoretti.geotasks.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.matteomoretti.geotasks.ui.viewmodel.TasksUiState
import com.matteomoretti.geotasks.ui.theme.GeoTasksTheme
import com.matteomoretti.geotasks.ui.viewmodel.TaskFilter
import com.matteomoretti.geotasks.R

@Composable
fun TaskFormSection(
    uiState: TasksUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onLocationLabelChange: (String) -> Unit,
    onUseCurrentLocationClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    isAddEnabled: Boolean,
    isLocationButtonEnabled: Boolean,
    isSavingTask: Boolean,
    onCancelClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.task_title)) },
            singleLine = true,
            enabled = !isSavingTask
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.task_description)) },
            singleLine = true,
            enabled = !isSavingTask
        )

        OutlinedTextField(
            value = uiState.latitude,
            onValueChange = onLatitudeChange,
            label = { Text(stringResource(R.string.latitude)) },
            singleLine = true,
            enabled = !isSavingTask
        )

        OutlinedTextField(
            value = uiState.longitude,
            onValueChange = onLongitudeChange,
            label = { Text(stringResource(R.string.longitude)) },
            singleLine = true,
            enabled = !isSavingTask
        )

        OutlinedTextField(
            value = uiState.locationLabel,
            onValueChange = onLocationLabelChange,
            label = { Text(stringResource(R.string.location_label)) },
            singleLine = true,
            enabled = isLocationButtonEnabled && !isSavingTask
        )

        Button(
            onClick = onUseCurrentLocationClick,
            enabled = isLocationButtonEnabled
        ) {
            Text(
                if (uiState.isLoadingLocation) {
                    stringResource(R.string.loading_location)
                } else {
                    stringResource(R.string.use_current_location)
                }
            )
        }

        Text(
            text = uiState.locationStatus,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onCancelClick,
            enabled = !isSavingTask
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskFormSectionPreview() {
    GeoTasksTheme {
        TaskFormSection(
            uiState = TasksUiState(
                title = "Buy milk",
                description = "2 bottles",
                latitude = "43.7700",
                longitude = "11.2500",
                locationLabel = "Home",
                locationStatus = "Location received",
                selectedFilter = TaskFilter.ALL,
                isLoadingLocation = false
            ),
            onTitleChange = {},
            onDescriptionChange = {},
            onLatitudeChange = {},
            onLongitudeChange = {},
            onLocationLabelChange = {},
            onUseCurrentLocationClick = {},
            onAddTaskClick = {},
            isAddEnabled = true,
            isLocationButtonEnabled = true,
            isSavingTask = false,
            onCancelClick = {}
        )
    }
}