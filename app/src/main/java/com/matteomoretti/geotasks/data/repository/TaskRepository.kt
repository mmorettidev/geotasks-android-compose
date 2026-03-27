package com.matteomoretti.geotasks.data.repository

import com.matteomoretti.geotasks.data.local.dao.TaskDao
import com.matteomoretti.geotasks.data.local.toTask
import com.matteomoretti.geotasks.data.local.toTaskEntity
import com.matteomoretti.geotasks.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun observeTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    suspend fun seedSampleTasksIfEmpty() {
        val existingTasks = taskDao.observeAllTasks().first()

        if (existingTasks.isNotEmpty()) return

        getSampleTasks().forEach { task ->
            taskDao.insertTask(task.toTaskEntity())
        }
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toTaskEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toTaskEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toTaskEntity())
    }

    private fun getSampleTasks(): List<Task> {
        return listOf(
            Task(
                id = 1L,
                title = "Buy cables near office",
                description = "Need HDMI adapter and USB-C cable",
                latitude = 43.7696,
                longitude = 11.2558,
                isCompleted = false,
                createdAt = 1710000000000L,
                locationLabel = "Sample location",
            ),
            Task(
                id = 2L,
                title = "Pick up documents",
                description = "Collect signed papers from coworking space",
                latitude = 43.7710,
                longitude = 11.2486,
                isCompleted = true,
                createdAt = 1710000001000L,
                locationLabel = "Sample location",
            ),
            Task(
                id = 3L,
                title = "Check survey point",
                description = "Verify GPS position for field note",
                latitude = 43.7682,
                longitude = 11.2623,
                isCompleted = false,
                createdAt = 1710000002000L,
                locationLabel = "Sample location",
            )
        )
    }
}