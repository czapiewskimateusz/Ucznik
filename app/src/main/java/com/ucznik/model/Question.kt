package com.ucznik.model

/**
 * Created by Mateusz on 22.02.2018.
 */
data class Question(var questionId: Int, var TopicId: Int, var question: String, var answer: String, var done: Boolean) {

    companion object {
        fun getSampleData(): ArrayList<Question> {
            val questions = ArrayList<Question>()
            questions.add(Question(1,1,"W którym roku był chrzest polski?","966r",false))
            questions.add(Question(11,1,"W którym roku był chrzest polski?","966r",true))
            questions.add(Question(12,1,"W którym roku był chrzest polski?","966r",false))
            questions.add(Question(13,1,"W którym roku był chrzest polski?","966r",false))
            questions.add(Question(14,1,"W którym roku był chrzest polski?","966r",true))
            questions.add(Question(15,1,"W którym roku był chrzest polski?","966r",true))
            questions.add(Question(16,1,"W którym roku był chrzest polski?","966r",false))
            questions.add(Question(17,1,"W którym roku był chrzest polski?","966r",true))
            questions.add(Question(18,1,"W którym roku był chrzest polski?","966r",false))
            return questions
        }
    }
}