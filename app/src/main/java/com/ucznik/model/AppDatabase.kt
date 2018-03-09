package com.ucznik.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.ucznik.model.dao.QuestionDAO
import com.ucznik.model.dao.TopicDAO
import com.ucznik.model.entities.Question
import com.ucznik.model.entities.Topic

/**
 * Created by Mateusz on 24.02.2018.
 */

@Database(entities = arrayOf(Topic::class,Question::class),version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun topicDAO(): TopicDAO
    abstract fun questionDAO(): QuestionDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "appDatabase.db")
                            .fallbackToDestructiveMigration()
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