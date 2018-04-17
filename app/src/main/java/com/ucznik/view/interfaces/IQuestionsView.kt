package com.ucznik.view.interfaces

/**
 * Created by Mateusz on 27.02.2018.
 */
interface IQuestionsView {
    fun updateQuestionStatus(status: String)
    fun setStatusColor(color: Int)
    fun scrollToPosition(i: Int)
    fun alreadyLearned()
}