package com.ucznik.model.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.ucznik.model.entities.Question

/**
* Created by Mateusz on 24.02.2018.
*/
@Dao interface QuestionDAO {

    @Query("select * from question where topicId = :arg0")
    fun getAllQuestions(topicId: Long): List<Question>

    @Query("select * from question where questionId = :arg0")
    fun findQuestionById(id: Long): Question

    @Insert(onConflict = REPLACE)
    fun insertQuestion(question: Question)

    @Update(onConflict = REPLACE)
    fun updateQuestion(question: Question)

    @Delete
    fun deleteQuestion(question: Question)
}