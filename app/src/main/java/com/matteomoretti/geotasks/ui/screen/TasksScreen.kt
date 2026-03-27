package com.matteomoretti.geotasks.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.matteomoretti.geotasks.R
import com.matteomoretti.geotasks.location.LocationHelper
import com.matteomoretti.geotasks.ui.viewmodel.TaskFilter
import com.matteomoretti.geotasks.ui.viewmodel.TasksViewModel
import com.matteomoretti.geotasks.util.UiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel = viewModel()
) {
    val tasks = viewModel.tasks.collectAsState().value
    val uiState = viewModel.uiState.collectAsState().value
    var taskToDeleteId by remember { mutableStateOf<Long?>(null) }
    val filteredTasks = when (uiState.selectedFilter) {
        TaskFilter.ALL -> tasks
        TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
    }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            viewModel.updateLocationStatus(UiConstants.PERMISSION_GRANTED_REQUESTING_LOCATION)
            viewModel.updateIsLoadingLocation(true)

            locationHelper.getCurrentLocation(
                onLocationReceived = { latitude, longitude ->
                    viewModel.updateLatitude(latitude.toString())
                    viewModel.updateLongitude(longitude.toString())
                    viewModel.updateLocationLabel(UiConstants.CURRENT_LOCATION)
                    viewModel.updateLocationStatus(UiConstants.LOCATION_RECEIVED)
                    viewModel.updateIsLoadingLocation(false)
                },
                onError = {
                    viewModel.updateLocationStatus(UiConstants.LOCATION_NOT_AVAILABLE)
                    viewModel.updateIsLoadingLocation(false)
                }
            )
        } else {
            viewModel.updateLocationStatus(UiConstants.PERMISSION_DENIED)
            viewModel.updateIsLoadingLocation(false)
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    val fabScale = animateFloatAsState(
        targetValue = if (uiState.isFormVisible) 1.08f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = "fabScale"
    )

    LaunchedEffect(uiState.locationStatus) {
        if (uiState.locationStatus.isNotBlank()) {
            snackBarHostState.showSnackbar(uiState.locationStatus)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GeoTasks") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!uiState.isFormVisible) {
                        viewModel.toggleFormVisibility()
                    } else {
                        if (uiState.title.isBlank()) {
                            viewModel.updateLocationStatus(UiConstants.TITLE_REQUIRED)
                        } else {
                            viewModel.addTask(
                                uiState.title,
                                uiState.description,
                                uiState.latitude,
                                uiState.longitude,
                                uiState.locationLabel
                            )
                        }
                    }
                },
                modifier = Modifier.scale(fabScale.value),
                containerColor = if (uiState.isFormVisible) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                contentColor = if (uiState.isFormVisible) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            ) {
                Icon(
                    imageVector = if (uiState.isFormVisible) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.Add
                    },
                    contentDescription = if (uiState.isFormVisible) {
                        "Save task"
                    } else {
                        "Open task form"
                    }
                )
            }
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            Text(
                text = "GeoTasks",
                style = MaterialTheme.typography.headlineMedium
            )

            TaskFilterSection(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { viewModel.updateFilter(it) }
            )
            AnimatedVisibility(
                visible = uiState.isFormVisible,
                enter = fadeIn() + slideInVertically { -it / 2 },
                exit = fadeOut() + slideOutVertically { -it / 2 }
            )   {
                TaskFormSection(
                    uiState = uiState,
                    onTitleChange = { viewModel.updateTitle(it) },
                    onDescriptionChange = { viewModel.updateDescription(it) },
                    onLatitudeChange = { viewModel.updateLatitude(it) },
                    onLongitudeChange = { viewModel.updateLongitude(it) },
                    onLocationLabelChange = { viewModel.updateLocationLabel(it) },
                    onUseCurrentLocationClick = {
                        requestCurrentLocation(
                            context = context,
                            locationHelper = locationHelper,
                            permissionLauncher = permissionLauncher,
                            onRequestingPermission = {
                                viewModel.updateLocationStatus(UiConstants.REQUESTING_PERMISSION)
                                viewModel.updateIsLoadingLocation(false)
                            },
                            onPermissionGranted = {
                                viewModel.updateLocationStatus(UiConstants.PERMISSION_GRANTED_REQUESTING_LOCATION)
                                viewModel.updateIsLoadingLocation(true)
                            },
                            onLocationReceived = { latitude, longitude ->
                                viewModel.updateLatitude(latitude.toString())
                                viewModel.updateLongitude(longitude.toString())
                                viewModel.updateLocationLabel(UiConstants.CURRENT_LOCATION)
                                viewModel.updateLocationStatus(UiConstants.LOCATION_RECEIVED)
                                viewModel.updateIsLoadingLocation(false)
                            },
                            onLocationError = {
                                viewModel.updateLocationStatus(UiConstants.LOCATION_NOT_AVAILABLE)
                                viewModel.updateIsLoadingLocation(false)
                            }
                        )
                    },
                    onAddTaskClick = {
                        if (uiState.title.isBlank()) {
                            viewModel.updateLocationStatus(UiConstants.TITLE_REQUIRED)
                        } else {
                            viewModel.addTask(
                                uiState.title,
                                uiState.description,
                                uiState.latitude,
                                uiState.longitude,
                                uiState.locationLabel
                            )
                        }
                    },
                    isAddEnabled = uiState.title.isNotBlank(),
                    isLocationButtonEnabled = !uiState.isLoadingLocation,
                    isSavingTask = uiState.isSavingTask,
                    onCancelClick = {
                        viewModel.updateTitle("")
                        viewModel.updateDescription("")
                        viewModel.updateLatitude("")
                        viewModel.updateLongitude("")
                        viewModel.updateLocationLabel("")
                        viewModel.updateLocationStatus(UiConstants.LOCATION_NOT_REQUESTED)
                        if (uiState.isFormVisible) {
                            viewModel.toggleFormVisibility()
                        }
                    },
                )
            }

            TaskListSection(
                tasks = filteredTasks,
                onTaskClick = { viewModel.toggleTaskCompletion(it) },
                onDeleteClick = { taskToDeleteId = it },
                onRenameClick = { viewModel.renameTask(it) }
            )

            Text(
                text = stringResource(
                    R.string.showing_tasks,
                    filteredTasks.size,
                    if (filteredTasks.size == 1) "" else "s"
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            if (taskToDeleteId != null) {
                DeleteTaskDialog(
                    onConfirm = {
                        viewModel.deleteTask(taskToDeleteId!!)
                        taskToDeleteId = null
                    },
                    onDismiss = {
                        taskToDeleteId = null
                    }
                )
            }
        }
    }
}

private fun requestCurrentLocation(
    context: android.content.Context,
    locationHelper: LocationHelper,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    onRequestingPermission: () -> Unit,
    onPermissionGranted: () -> Unit,
    onLocationReceived: (Double, Double) -> Unit,
    onLocationError: () -> Unit
) {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (fineGranted || coarseGranted) {
        onPermissionGranted()

        locationHelper.getCurrentLocation(
            onLocationReceived = onLocationReceived,
            onError = onLocationError
        )
    } else {
        onRequestingPermission()

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
}