package com.ucznik.presenter

import android.content.Context
import android.os.AsyncTask
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Question
import com.ucznik.view.interfaces.ILearnView

class LearnPresenter(val view: ILearnView,
                     private val context: Context) {

    private var questions = ArrayList<Question>()
    private var questionsLearned = ArrayList<Question>()
    private var numberOfQuestions = 0
    var topicId: Long? = null

    fun loadQuestions(topicId: Long) {
        this.topicId = topicId
        GetQuestionsAsyncTask(this).execute(topicId)
    }

    private fun showNextQuestion() {
        if (questions.size == 0) view.learningDone()
        else {
            questions.shuffle()
            view.displayQuestion(questions[0])
        }
    }

    private fun startLearning() {
        view.updateStatus("${questionsLearned.size}/$numberOfQuestions")
        questions.shuffle()
        view.displayQuestion(questions[0])
    }

    private fun splitToLearned() {
        questions.forEach {
            if (it.done == 1) questionsLearned.add(it)
        }
        questions.removeAll(questionsLearned)
    }

    fun doNotKnowAnswer() {
        view.showAnswer(true)
    }

    fun knowAnswer(ok: String) {
        if (ok == "OK") {
            view.hideAnswer()
            showNextQuestion()
        }
        else if (questions.size > 0) {
            updateLearned()
            view.updateStatus("${questionsLearned.size}/$numberOfQuestions")
            showNextQuestion()
        }
    }

    private fun updateLearned() {
        questionsLearned.add(questions[0])
        val question = questions[0]
        question.done = 1
        updateQuestionDB(question)
        questions.removeAt(0)
    }

    private fun updateQuestionDB(question: Question) {
        AsyncTask.execute {
            run {
                AppDatabase.getInstance(context)!!.questionDAO().updateQuestion(question)
            }
        }
    }

    companion object {
        class GetQuestionsAsyncTask(private val learnPresenter: LearnPresenter) : AsyncTask<Long, Int, ArrayList<Question>>() {

            override fun doInBackground(vararg topicId: Long?): ArrayList<Question> {
                learnPresenter.questions.clear()
                learnPresenter.questions.addAll(AppDatabase.getInstance(learnPresenter.context)!!.questionDAO().getAllQuestions(topicId[0]!!))
                return learnPresenter.questions
            }

            override fun onPostExecute(result: ArrayList<Question>?) {
                learnPresenter.numberOfQuestions = result?.size ?: 0
                learnPresenter.splitToLearned()
                if (learnPresenter.questions.size != 0) learnPresenter.startLearning()
            }
        }
    }
}
