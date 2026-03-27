package com.matteomoretti.geotasks.ui.viewmodel

import com.matteomoretti.geotasks.util.UiConstants

data class TasksUiState(
    val title: String = "",
    val description: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val locationLabel: String = "",
    val locationStatus: String = UiConstants.LOCATION_NOT_REQUESTED,
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val isLoadingLocation: Boolean = false,
    val isSavingTask: Boolean = false,
    val isFormVisible: Boolean = true
)