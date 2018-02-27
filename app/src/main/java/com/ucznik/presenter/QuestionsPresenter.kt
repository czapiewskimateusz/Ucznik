package com.ucznik.presenter


import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Color
import android.support.v4.app.FragmentActivity
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView

/**
 * Created by Mateusz on 27.02.2018.
 */
class QuestionsPresenter(val view: IQuestionsView,
                         private val context: Context,
                         private val activity: FragmentActivity) : QuestionsAdapter.QuestionsAdapterListener {

    private var questions = Question.getSampleData()
    var questionsAdapter = QuestionsAdapter(questions, context, this)
    var topicId: Long? = null

    fun changeStatus() {
        var done = 0.0
        questions.forEach { q -> if (q.done == 1) done++ }
        val percentage: Double = (done / questions.size) * 100
        view.updateQuestionStatus(String.format("%.2f", percentage) + "%")
        if (percentage < 50.0) {
            view.setStatusColor(Color.RED)
            return
        }
        if (percentage <= 80.0) {
            view.setStatusColor(Color.YELLOW)
            return
        } else view.setStatusColor(Color.GREEN)
    }

    override fun markedDone() {
        changeStatus()
    }

    override fun markedUndone() {
        changeStatus()
    }

    override fun questionClicked(question: Question) {
        showDialog(question)
    }

    private fun showDialog(question:Question) {
        val fragmentManager = activity.supportFragmentManager
        val questionEditDialog = QuestionEditDialog()
        questionEditDialog.oldQuestion = question
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, questionEditDialog)
                .addToBackStack(null).commit()
    }

    fun updateQuestion(questionEditDialog: QuestionEditDialog) {
        questions.forEachIndexed(action = { index, question ->
            if (question.questionId == questionEditDialog.oldQuestion?.questionId){
                question.question = questionEditDialog.question!!
                question.answer = questionEditDialog.answer!!
                questionsAdapter.notifyItemChanged(index)

                hideDialog(questionEditDialog)
                return
            }
        })
    }

    fun hideDialog(questionEditDialog: QuestionEditDialog) {
        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        transaction.remove(questionEditDialog).commit()
    }
}