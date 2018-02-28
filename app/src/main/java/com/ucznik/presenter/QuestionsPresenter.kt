package com.ucznik.presenter


import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import com.ucznik.model.db.AppDatabase
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mateusz on 27.02.2018.
 */
class QuestionsPresenter(val view: IQuestionsView,
                         private val context: Context,
                         private val activity: FragmentActivity) : QuestionsAdapter.QuestionsAdapterListener {

    private val compositeDisposable = CompositeDisposable()
    private var questions = ArrayList<Question>()
    var questionsAdapter = QuestionsAdapter(questions, context, this)
    var topicId: Long? = null
    private var dataBase: AppDatabase? = null

    fun loadData(){
        dataBase = AppDatabase.getInstance(context)
        fetchQuestionDataFromDB()
        changeStatus()
    }

    private fun changeStatus() {
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

    override fun updateStatus(position: Int) {
        updateQuestionDB(questions[position])
        changeStatus()
    }

    private fun fetchQuestionDataFromDB() {
        compositeDisposable.add(dataBase!!.questionDAO().getAllQuestions(topicId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    questions.clear()
                    questions.addAll(it)
                    questionsAdapter.notifyDataSetChanged()
                }))
    }

    private fun updateQuestionDB(question: Question) {
        compositeDisposable.add(Observable.fromCallable { dataBase!!.questionDAO().updateQuestion(question) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun insertQuestionDB(question: Question) {
        compositeDisposable.add(Observable.fromCallable { dataBase!!.questionDAO().insertQuestion(question) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun deleteQuestionDB(question: Question) {
        compositeDisposable.add(Observable.fromCallable { dataBase!!.questionDAO().deleteQuestion(question) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun questionClicked(question: Question) {
        showDialog(question)
    }

    fun showDialog(question: Question?) {
        val fragmentManager = activity.supportFragmentManager
        val questionEditDialog = QuestionEditDialog()
        questionEditDialog.oldQuestion = question
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, questionEditDialog)
                .addToBackStack(null).commit()
    }

    fun hideDialog(questionEditDialog: QuestionEditDialog) {
        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        transaction.remove(questionEditDialog).commit()
    }

    fun updateQuestion(questionEditDialog: QuestionEditDialog) {
        if (questionEditDialog.oldQuestion == null) {
            val question = Question(topicId!!, questionEditDialog.question!!, questionEditDialog.answer!!, 0)
            insertQuestionDB(question)
            questions.add(question)
            questionsAdapter.notifyItemInserted(questions.size - 1)
            view.scrollToPosition(questions.size - 1)
            hideDialog(questionEditDialog)
        } else {
            questions.forEachIndexed(action = { index, question ->
                if (question.questionId == questionEditDialog.oldQuestion?.questionId) {
                    question.question = questionEditDialog.question!!
                    question.answer = questionEditDialog.answer!!
                    updateQuestionDB(question)
                    questionsAdapter.notifyItemChanged(index)
                    hideDialog(questionEditDialog)
                    return
                }
            })
        }
    }

    fun removeAt(adapterPosition: Int) {
        val question = questions[adapterPosition]
        showSnackBar(question, adapterPosition)
        deleteQuestionDB(question)
        questions.removeAt(adapterPosition)
        questionsAdapter.notifyItemRemoved(adapterPosition)
        questionsAdapter.notifyItemRangeChanged(adapterPosition, questions.size - 1)
    }

    private fun addAt(question: Question, adapterPosition: Int) {
        insertQuestionDB(question)
        questions.add(adapterPosition, question)
        questionsAdapter.notifyItemInserted(adapterPosition)
        questionsAdapter.notifyItemRangeChanged(adapterPosition, questions.size - 1)
    }

    private fun showSnackBar(question: Question, position: Int) {
        Snackbar.make(activity.currentFocus, question.question, Snackbar.LENGTH_LONG).setAction(context.getString(R.string.cancel), {
            addAt(question, position)
        }).show()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        AppDatabase.destroyInstance()
    }
}