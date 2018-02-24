package com.ucznik.model.entities

import android.arch.persistence.room.*


/**
 * Created by Mateusz on 22.02.2018.
 */
@Entity(foreignKeys = arrayOf(ForeignKey(entity = Topic::class,parentColumns = arrayOf("topicId"),childColumns = arrayOf("topicId"))))
data class Question(@PrimaryKey(autoGenerate = true) var questionId: Long,
                    var topicId: Long,
                    var question: String,
                    var answer: String,
                    var done: Int) {

    companion object {
        fun getSampleData(): ArrayList<Question> {
            val questions = ArrayList<Question>()
            questions.add(Question(1, 1, "W którym roku był chrzest polski?", "966r", 0))
            questions.add(Question(11, 1, "W którym roku był chrzest polski?", "966r", 1))
            questions.add(Question(12, 1, "W którym roku był chrzest polski?", "966r", 0))
            questions.add(Question(13, 1, "W którym roku był chrzest polski?", "966r", 0))
            questions.add(Question(14, 1, "W którym roku był chrzest polski?", "966r", 1))
            questions.add(Question(15, 1, "W którym roku był chrzest polski?", "966r", 1))
            questions.add(Question(16, 1, "W którym roku był chrzest polski?", "966r", 0))
            questions.add(Question(17, 1, "W którym roku był chrzest polski?", "966r", 1))
            questions.add(Question(18, 1, "W którym roku był chrzest polski?", "966r", 0))
            return questions
        }
    }
}