package com.matteomoretti.geotasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.matteomoretti.geotasks.data.local.dao.TaskDao
import com.matteomoretti.geotasks.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}