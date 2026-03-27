package com.matteomoretti.geotasks.data.local

import com.matteomoretti.geotasks.data.local.entity.TaskEntity
import com.matteomoretti.geotasks.data.model.Task

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        locationLabel = locationLabel,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        locationLabel = locationLabel,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}