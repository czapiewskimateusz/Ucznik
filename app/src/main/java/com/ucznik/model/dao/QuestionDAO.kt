package com.ucznik.model.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.ucznik.model.entities.Question

/**
* Created by Mateusz on 24.02.2018.
*/
@Dao interface QuestionDAO {

    @Query("select * from question where topicId = :topicId")
    fun getAllQuestions(topicId: Long): List<Question>

    @Query("update question set done = 0 where topicId = :topicId")
    fun resetAllQuestions(topicId: Long)

    @Query("select * from question where questionId = :id")
    fun findQuestionById(id: Long): Question

    @Insert(onConflict = REPLACE)
    fun insertQuestion(question: Question):Long

    @Update(onConflict = REPLACE)
    fun updateQuestion(question: Question)

    @Delete
    fun deleteQuestion(question: Question)
}