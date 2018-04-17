package com.ucznik.presenter

import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.activities.LearnActivity
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView

class QuestionsPresenter(val view: IQuestionsView,
                         private val context: Context,
                         private val activity: FragmentActivity) : QuestionsAdapter.QuestionsAdapterListener {

    private var questions = ArrayList<Question>()
    var questionsAdapter = QuestionsAdapter(questions, context, this)
    var topicId: Long? = null

    fun loadData(topicId: Long) {
        this.topicId = topicId
        fetchQuestionDataFromDB()
    }

    fun onDestroy() {
        AppDatabase.destroyInstance()
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

    override fun updateStatus(id: Long) {
        updateQuestion(getQuestionById(id)!!)
        changeStatus()
    }

    private fun getQuestionById(id: Long): Question? {
        questions.forEach({
            if (it.questionId == id) {
                return it
            }
        })
        return null
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
                    performUpdate(question, questionEditDialog)
                    return
                }
            })
        }
    }

    private fun performUpdate(question: Question, questionEditDialog: QuestionEditDialog) {
        question.question = questionEditDialog.question!!
        question.answer = questionEditDialog.answer!!
        questionsAdapter.update(question)
        hideDialog(questionEditDialog)
        updateQuestion(question)
    }

    private fun addNewQuestion(questionEditDialog: QuestionEditDialog) {
        val question = Question(topicId!!, questionEditDialog.question!!, questionEditDialog.answer!!, 0)
        questions.add(question)
        questionsAdapter.add(question)
        hideDialog(questionEditDialog)
        changeStatus()
        insertQuestion(question)
    }

    fun removeQuestion(adapterPosition: Int) {
        val question = questionsAdapter.getQuestion(adapterPosition)
        showSnackBar(question, adapterPosition)
        questions.remove(question)
        questionsAdapter.remove(question)
        deleteQuestion(question)
        changeStatus()
    }

    private fun recoverQuestion(question: Question, adapterPosition: Int) {
        questions.add(adapterPosition, question)
        questionsAdapter.add(question)
        insertQuestion(question)
        changeStatus()
    }

    private fun showSnackBar(question: Question, position: Int) {
        Snackbar.make(activity.currentFocus, question.question, Snackbar.LENGTH_LONG).setAction(context.getString(R.string.cancel), {
            recoverQuestion(question, position)
        }).show()
    }

    fun onQueryTextChange(query: String?) {
        val filteredModeList = filterQuestions(questions, query!!)
        questionsAdapter.replaceAll(filteredModeList)
        view.scrollToPosition(0)
    }

    private fun filterQuestions(questions: List<Question>, query: String): List<Question> {
        val lowerCaseQuery = query.toLowerCase()
        val filteredModelList = ArrayList<Question>()
        questions.forEach {
            val text = it.question.toLowerCase()
            if (text.contains(lowerCaseQuery)) filteredModelList.add(it)
        }
        return filteredModelList
    }

    private fun fetchQuestionDataFromDB() {
        val task = DatabaseGetQuestions(this)
        task.execute(topicId)
    }

    private fun updateQuestion(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().updateQuestion(question)
            }
        })
    }

    private fun insertQuestion(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().insertQuestion(question)
            }
        })
    }

    private fun deleteQuestion(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().deleteQuestion(question)
            }
        })
    }

    fun startLearning(): Boolean {
        if (allLearned()) showAlreadyLearnedToast()
        else {
            startLearningActivity()
        }
        return true
    }

    private fun startLearningActivity() {
        val intent = Intent(context, LearnActivity::class.java)
        intent.putExtra(TOPIC_ID_EXTRA, topicId)
        context.startActivity(intent)
    }

    private fun showAlreadyLearnedToast() {
        Toast.makeText(context, context.getText(R.string.already_learned), Toast.LENGTH_SHORT).show()
    }

    private fun allLearned(): Boolean {
        questions.forEach({
            if (it.done == 0) return false
        })
        return true
    }

    companion object {
        class DatabaseGetQuestions(private val questionsPresenter: QuestionsPresenter) : AsyncTask<Long, Int, ArrayList<Question>>() {

            override fun doInBackground(vararg topicId: Long?): ArrayList<Question> {
                questionsPresenter.questions.clear()
                questionsPresenter.questions.addAll(AppDatabase.getInstance(questionsPresenter.context)!!.questionDAO().getAllQuestions(topicId[0]!!))
                return questionsPresenter.questions
            }

            override fun onPostExecute(result: ArrayList<Question>?) {
                questionsPresenter.questionsAdapter.addAll(result!!)
                questionsPresenter.changeStatus()
                questionsPresenter.questionsAdapter.notifyDataSetChanged()
            }
        }
    }
}

