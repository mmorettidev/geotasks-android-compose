package com.matteomoretti.geotasks.data.model

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val locationLabel: String,
    val isCompleted: Boolean,
    val createdAt: Long
)