package com.ucznik.presenter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.activities.LearnActivity
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView
import android.content.ContextWrapper
import android.net.Uri
import android.support.v4.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class QuestionsPresenter(val view: IQuestionsView,
                         private val context: Context,
                         private val activity: FragmentActivity) : QuestionsAdapter.QuestionsAdapterListener {

    private var questions = ArrayList<Question>()
    var questionsAdapter = QuestionsAdapter(questions, context, this)
    var topicId: Long? = null

    fun loadData(topicId: Long) {
        this.topicId = topicId
        DatabaseGetQuestions(this).execute(topicId)
    }

    private fun changeStatus() {
        var done = 0.0
        questions.forEach { q -> if (q.done == 1) done++ }
        if (done == questions.size.toDouble() && questions.size > 0) markTopicDoneDB()
        else markTopicUnDoneDB()
        checkPercentage(done)
    }

    private fun checkPercentage(done: Double) {
        val percentage: Double = (done / questions.size) * 100
        view.updateQuestionStatus(String.format("%.2f", percentage) + "%")
        when {
            percentage < 50.0 -> view.setStatusColor(Color.RED)
            percentage <= 80.0 -> view.setStatusColor(Color.YELLOW)
            else -> view.setStatusColor(Color.GREEN)
        }
    }

    override fun updateStatus(id: Long) {
        updateQuestionDB(getQuestionById(id)!!)
        changeStatus()
    }

    private fun getQuestionById(id: Long): Question? {
        questions.forEach({
            if (it.questionId == id) return it
        })
        return null
    }

    override fun questionClicked(question: Question) {
        showDialog(question)
    }

    fun showDialog(question: Question?) {
        val questionEditDialog = QuestionEditDialog()
        questionEditDialog.oldQuestion = question
        questionEditDialog.show(activity.supportFragmentManager,"edit_q")
    }

    fun hideDialog(questionEditDialog: QuestionEditDialog) {
       questionEditDialog.dismiss()
    }

    fun updateQuestionDB(questionEditDialog: QuestionEditDialog) {
        if (questionEditDialog.oldQuestion == null) addNewQuestion(questionEditDialog)
        else {
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

        if (questionEditDialog.bitmap != null) replaceImage(question, questionEditDialog)
        else deleteImage(question)

        updateQuestionDB(question)
        questionsAdapter.update(question)
        hideDialog(questionEditDialog)
    }

    private fun deleteImage(question: Question) {
        try {
            val file = File(question.image)
            file.delete()
        } catch (e: NullPointerException) {
        }
        question.image = null
    }

    private fun replaceImage(question: Question, questionEditDialog: QuestionEditDialog) {
        try {
            val file = File(question.image)
            file.delete()
        } catch (e: NullPointerException) {
        }
        question.image = saveToFile(questionEditDialog.bitmap)
    }

    private fun addNewQuestion(questionEditDialog: QuestionEditDialog) {
        val path = saveToFile(questionEditDialog.bitmap)
        val question = Question(topicId!!, questionEditDialog.question!!, questionEditDialog.answer!!,path,0)
        questions.add(question)
        questionsAdapter.add(question)
        hideDialog(questionEditDialog)
        changeStatus()
        insertQuestionDB(question)
    }

    private fun saveToFile(bitmap: Bitmap?):String? {
        if (bitmap == null) return null
        val file = initFile()
        return try{
            compress(file!!, bitmap)
        } catch (e:IOException){
            e.printStackTrace()
            null
        }
    }

    private fun initFile(): File? {
        val wrapper = ContextWrapper(context)
        var file = wrapper.getDir("Images", MODE_PRIVATE)
        val c = Calendar.getInstance()
        val name = c.get(Calendar.MILLISECOND).toString() + "_" + c.get(Calendar.DATE).toString()
        file = File(file, "$name.jpg")
        return file
    }

    private fun compress(file: File, bitmap: Bitmap): String {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        stream.flush()
        stream.close()
        return Uri.parse(file.absolutePath).toString()
    }

    fun removeQuestion(adapterPosition: Int) {
        val question = questionsAdapter.getQuestion(adapterPosition)
        showSnackBar(question, adapterPosition)
        questions.remove(question)
        questionsAdapter.remove(question)
        deleteQuestionDB(question)
        changeStatus()
    }

    private fun recoverQuestion(question: Question, adapterPosition: Int) {
        questions.add(adapterPosition, question)
        questionsAdapter.add(question)
        insertQuestionDB(question)
        changeStatus()
    }

    private fun showSnackBar(question: Question, position: Int) {
       val snackbar = Snackbar.make(activity.currentFocus, question.question, Snackbar.LENGTH_LONG).setAction(context.getString(R.string.cancel), {
            recoverQuestion(question, position)
        })
        snackbar.view.setBackgroundColor(ContextCompat.getColor(context,R.color.primary))
        snackbar.show()
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

    fun startLearning(): Boolean {
        if (allLearned()) view.alreadyLearned()
        else startLearningActivity()
        return true
    }

    private fun startLearningActivity() {
        val intent = Intent(context, LearnActivity::class.java)
        intent.putExtra(TOPIC_ID_EXTRA, topicId)
        context.startActivity(intent)
    }

    private fun allLearned(): Boolean {
        questions.forEach({
            if (it.done == 0) return false
        })
        return true
    }

    private fun updateQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().updateQuestion(question)
            }
        })
    }

    private fun insertQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().insertQuestion(question)
            }
        })
    }

    private fun deleteQuestionDB(question: Question) {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.questionDAO().deleteQuestion(question)
            }
        })
    }

    private fun markTopicDoneDB() {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.topicDAO().markDone(topicId!!)
            }
        })
    }

    private fun markTopicUnDoneDB() {
        AsyncTask.execute({
            run {
                AppDatabase.getInstance(context)!!.topicDAO().markUnDone(topicId!!)
            }
        })
    }

    companion object {
        class DatabaseGetQuestions(private val questionsPresenter: QuestionsPresenter) : AsyncTask<Long, Int, List<Question>>() {

            override fun doInBackground(vararg topicId: Long?): List<Question> {
                return  AppDatabase.getInstance(questionsPresenter.context)!!.questionDAO().getAllQuestions(topicId[0]!!)
            }

            override fun onPostExecute(result: List<Question>?) {
                questionsPresenter.questions.clear()
                questionsPresenter.questions.addAll(result!!)
                questionsPresenter.questionsAdapter.addAll(result)
                questionsPresenter.questionsAdapter.notifyDataSetChanged()
                questionsPresenter.changeStatus()
            }
        }
    }
}

