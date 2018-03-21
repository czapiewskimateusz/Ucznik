package com.ucznik.model.entities

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

/**
* Created by Mateusz on 22.02.2018.
*/
@Entity(foreignKeys = arrayOf(ForeignKey(entity = Topic::class, parentColumns = arrayOf("topicId"), childColumns = arrayOf("topicId"),onDelete=CASCADE)))
data class Question(var topicId: Long,
                    var question: String,
                    var answer: String,
                    var done: Int){

    @PrimaryKey(autoGenerate = true)
    var questionId: Long = 0
}