package com.ucznik.view.interfaces

import com.ucznik.model.entities.Question

/**
 * Created by Mateusz on 27.02.2018.
 */
interface ILearnView {
    fun updateStatus(status: String)
    fun learningDone()
    fun displayQuestion(question: Question)
    fun showAnswer(changeButtons: Boolean)
    fun hideAnswer()

}