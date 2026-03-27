package com.matteomoretti.geotasks.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "geotasks_database"
            )
                .fallbackToDestructiveMigration(true)
                .build()

            database = instance
            instance
        }
    }
}