package com.ucznik.presenter

import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView

class QuestionsPresenter(val view: IQuestionsView,
                         private val context: Context,
                         private val activity: FragmentActivity) : QuestionsAdapter.QuestionsAdapterListener {

    private var questions = ArrayList<Question>()
    var questionsAdapter = QuestionsAdapter(questions, context, this)
    var topicId: Long? = null
    private var dataBase: AppDatabase? = null

    fun loadData(topicId: Long) {
        this.topicId = topicId
        dataBase = AppDatabase.getInstance(context)
        fetchQuestionDataFromDB()
    }

    private fun changeStatus() {
        var done = 0.0
        questions.forEach { q -> if (q.done == 1) done++ }
        val percentage: Double = (done / questions.size) * 100
        view.updateQuestionStatus(String.format("%.2f", percentage) + "%")
        when {
            percentage < 50.0 -> view.setStatusColor(Color.RED)
            percentage <= 80.0 -> view.setStatusColor(Color.YELLOW)
            else -> view.setStatusColor(Color.GREEN)
        }
    }

    override fun updateStatus(position: Int) {
        updateQuestionDB(questions[position])
        changeStatus()
    }

    override fun updateStatus(id: Long) {
        updateQuestionDB(getQuestionById(id))
        changeStatus()
    }

    private fun getQuestionById(id: Long): Question {
        var question: Question? = null
        questions.forEach({
            if (it.questionId == id) {
                question = it
            }
        })
        return question!!
    }

    override fun questionClicked(question: Question) {
        showDialog(question)
    }

    fun showDialog(question: Question?) {
        val questionEditDialog = QuestionEditDialog()
        questionEditDialog.oldQuestion = question
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, questionEditDialog).addToBackStack("dialog").commit()
    }

    fun hideDialog(questionEditDialog: QuestionEditDialog) {
        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        transaction.remove(questionEditDialog).commit()
        fragmentManager.popBackStack()
    }

    fun updateQuestion(questionEditDialog: QuestionEditDialog) {
        if (questionEditDialog.oldQuestion == null) {
            addNewQuestion(questionEditDialog)
        } else {
            questions.forEach(action = { question ->
                if (question.questionId == questionEditDialog.oldQuestion?.questionId) {
                    question.question = questionEditDialog.question!!
                    question.answer = questionEditDialog.answer!!
                    questionsAdapter.update(question)
                    hideDialog(questionEditDialog)
                    updateQuestionDB(question)
                    return
                }
            })
        }
    }

    private fun addNewQuestion(questionEditDialog: QuestionEditDialog) {
        val question = Question(topicId!!, questionEditDialog.question!!, questionEditDialog.answer!!, 0)
        insertQuestionDB(question)
        questions.add(question)
        questionsAdapter.add(question)
        //  questionsAdapter.notifyItemInserted(questions.size - 1)
        view.scrollToPosition(questions.size - 1)
        hideDialog(questionEditDialog)
        changeStatus()
    }

    fun removeQuestion(adapterPosition: Int) {
        val question = questionsAdapter.getQuestion(adapterPosition)
        showSnackBar(question, adapterPosition)
        removeQuestionById(question)
        questionsAdapter.remove(question)
        //  questionsAdapter.notifyItemRemoved(adapterPosition)
        //  questionsAdapter.notifyItemRangeChanged(adapterPosition, questions.size)
        deleteQuestionDB(question)
        changeStatus()
    }

    private fun removeQuestionById(question: Question) {
        var temp: Question? = null
        questions.forEach({
            if (it.questionId == question.questionId) temp = it
        })
        questions.remove(temp)
    }

    private fun recoverQuestion(question: Question, adapterPosition: Int) {
        questions.add(adapterPosition, question)
        questionsAdapter.add(question)
        insertQuestionDB(question)
    }

    private fun showSnackBar(question: Question, position: Int) {
        Snackbar.make(activity.currentFocus, question.question, Snackbar.LENGTH_LONG).setAction(context.getString(R.string.cancel), {
            recoverQuestion(question, position)
        }).show()
    }

    fun onDestroy() {
        AppDatabase.destroyInstance()
    }

    fun onQueryTextChange(query: String?) {
        val filteredModeList = filter(questions, query!!)
        questionsAdapter.replaceAll(filteredModeList)
        view.scrollToPosition(0)
    }

    private fun filter(questions: List<Question>, query: String): List<Question> {
        val lowerCaseQuery = query.toLowerCase()
        val filteredModelList = ArrayList<Question>()
        questions.forEach {
            val text = it.question.toLowerCase()
            if (text.contains(lowerCaseQuery)) filteredModelList.add(it)
        }
        return filteredModelList
    }

    private fun fetchQuestionDataFromDB() {
        AsyncTask.execute({
            run {
                questions.clear()
                questions.addAll(dataBase!!.questionDAO().getAllQuestions(topicId!!))
                questionsAdapter.addAll(questions)
                changeStatus()
            }
        })
    }

    private fun updateQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                dataBase!!.questionDAO().updateQuestion(question)
            }
        })
    }

    private fun insertQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                dataBase!!.questionDAO().insertQuestion(question)
            }
        })
    }

    private fun deleteQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                dataBase!!.questionDAO().deleteQuestion(question)
            }
        })
    }
}

