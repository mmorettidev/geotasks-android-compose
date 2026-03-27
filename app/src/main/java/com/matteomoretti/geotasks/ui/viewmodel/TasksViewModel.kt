package com.matteomoretti.geotasks.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.matteomoretti.geotasks.data.local.DatabaseProvider
import com.matteomoretti.geotasks.data.model.Task
import com.matteomoretti.geotasks.data.repository.TaskRepository
import com.matteomoretti.geotasks.util.UiConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TasksUiState())

    val uiState: StateFlow<TasksUiState> = _uiState

    private val repository = TaskRepository(
        DatabaseProvider.getDatabase(application).taskDao()
    )

    val tasks: StateFlow<List<Task>> = repository.observeTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.seedSampleTasksIfEmpty()
        }
    }

    fun addTask(
        title: String,
        description: String,
        latitudeText: String,
        longitudeText: String,
        locationLabel: String
    ) {
        if (title.isBlank()) return

        val parsedLatitude = latitudeText.toDoubleOrNull()
        val parsedLongitude = longitudeText.toDoubleOrNull()

        val latitude = if (parsedLatitude != null && parsedLatitude in -90.0..90.0) {
            parsedLatitude
        } else {
            43.7700
        }

        val longitude = if (parsedLongitude != null && parsedLongitude in -180.0..180.0) {
            parsedLongitude
        } else {
            11.2500
        }

        val finalDescription = if (description.isBlank()) {
            UiConstants.NO_DESCRIPTION
        } else {
            description.trim()
        }

        val finalLocationLabel = if (locationLabel.isBlank()) {
            UiConstants.CUSTOM_LOCATION
        } else {
            locationLabel.trim()
        }

        val newTask = Task(
            id = System.currentTimeMillis(),
            title = title.trim(),
            description = finalDescription,
            latitude = latitude,
            longitude = longitude,
            locationLabel = finalLocationLabel,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            updateIsSavingTask(true)
            delay(5000)
            repository.insertTask(newTask)

            _uiState.value = _uiState.value.copy(
                title = "",
                description = "",
                latitude = "",
                longitude = "",
                locationLabel = "",
                locationStatus = UiConstants.TASK_SAVED,
                isSavingTask = false,
                isFormVisible = false
            )
        }
    }

    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            val task = tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            repository.updateTask(
                task.copy(isCompleted = !task.isCompleted)
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            repository.deleteTask(task)
        }
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updateLatitude(value: String) {
        _uiState.value = _uiState.value.copy(latitude = value)
    }

    fun updateLongitude(value: String) {
        _uiState.value = _uiState.value.copy(longitude = value)
    }

    fun updateLocationLabel(value: String) {
        _uiState.value = _uiState.value.copy(locationLabel = value)
    }

    fun updateLocationStatus(value: String) {
        _uiState.value = _uiState.value.copy(locationStatus = value)
    }

    fun updateFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun renameTask(taskId: Long) {
        val newTitle = _uiState.value.title.trim()
        if (newTitle.isBlank()) {
            _uiState.value = _uiState.value.copy(
                locationStatus = UiConstants.ENTER_TITLE_BEFORE_RENAMING
            )
            return
        }

        viewModelScope.launch {
            val task = tasks.value.firstOrNull { it.id == taskId } ?: return@launch

            val updatedTask = task.copy(title = newTitle)
            repository.updateTask(updatedTask)

            _uiState.value = _uiState.value.copy(
                title = "",
                locationStatus = UiConstants.TASK_RENAMED
            )
        }
    }

    fun updateIsLoadingLocation(value: Boolean) {
        _uiState.value = _uiState.value.copy(isLoadingLocation = value)
    }

    fun updateIsSavingTask(value: Boolean) {
        _uiState.value = _uiState.value.copy(isSavingTask = value)
    }

    fun toggleFormVisibility() {
        _uiState.value = _uiState.value.copy(
            isFormVisible = !_uiState.value.isFormVisible
        )
    }
}