package com.ucznik.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.ucznik.model.dao.TopicDAO
import com.ucznik.model.entities.Question
import com.ucznik.model.entities.Topic

/**
 * Created by Mateusz on 24.02.2018.
 */

@Database(entities = arrayOf(Topic::class,Question::class),version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun topicDAO(): TopicDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "appDatabase.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}