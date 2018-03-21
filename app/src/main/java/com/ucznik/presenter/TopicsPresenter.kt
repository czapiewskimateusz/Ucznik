package com.ucznik.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.FragmentActivity
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Topic
import com.ucznik.presenter.adapters.TopicsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.activities.LoginActivity
import com.ucznik.view.activities.QuestionsActivity
import com.ucznik.view.dialogs.RenameDialog
import com.ucznik.view.interfaces.ITopicsView

/**
* Created by Mateusz on 23.02.2018.
*/
        const val TOPIC_ID_EXTRA = "TOPIC_ID_EXTRA"

class TopicsPresenter(val view: ITopicsView,
                      private val context: Context,
                      private val activity: FragmentActivity) : TopicsAdapter.TopicsAdapterCallback {

    private var topics = ArrayList<Topic>()
    var topicsAdapter = TopicsAdapter(topics, context, this)
    private var dataBase: AppDatabase? = null

    fun loadData() {
        dataBase = AppDatabase.getInstance(context)
        fetchTopicDataFromDB()
    }

    fun onDestroy() {
        AppDatabase.destroyInstance()
    }

    fun logout() {
        deleteUsersId()
        startLoginActivity()
    }

    @SuppressLint("CommitPrefEdits")
    private fun deleteUsersId() {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(context.getString(R.string.saved_user_id), -1)
            apply()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    override fun markDone(position: Int) {
        topics[position].done = 1
        topicsAdapter.notifyItemChanged(position)
        updateTopicDB(topics[position])
    }

    override fun markUndone(position: Int) {
        topics[position].done = 0
        topicsAdapter.notifyItemChanged(position)
        updateTopicDB(topics[position])
    }

    override fun showRenameDialog(position: Int) {
        val renameDialog = RenameDialog()
        renameDialog.currName = topics[position].name
        renameDialog.position = position
        renameDialog.show(activity.fragmentManager, "rename_dialog")
    }

    fun renameTopic(renameDialog: RenameDialog) {
        val topicName = renameDialog.newName.toString()
        if (renameDialog.position != null) {
            topics[renameDialog.position!!].name = topicName
            updateTopicDB(topics[renameDialog.position!!])
        } else addNewTopic(topicName)
    }

    private fun addNewTopic(topicName: String) {
        val newTopic = Topic(name = topicName, done = 0)
        addTopicDB(newTopic)
        topics.add(newTopic)
        topicsAdapter.notifyItemInserted(topics.size-1)
    }

    override fun deleteTopic(position: Int) {
        val deletedTopic = topics[position]
        topics.removeAt(position)
        topicsAdapter.notifyItemRemoved(position)
        topicsAdapter.notifyItemRangeChanged(position, topics.size - 1)
        deleteTopicDB(deletedTopic)
    }

    override fun showQuestions(topicId: Long) {
        val intent = Intent(context, QuestionsActivity::class.java)
        intent.putExtra(TOPIC_ID_EXTRA, topicId)
        context.startActivity(intent)
    }

    private fun fetchTopicDataFromDB() {
        AsyncTask.execute({
            run {
                topics.clear()
                topics.addAll(dataBase!!.topicDAO().getAllTopics())
                topicsAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun updateTopicDB(topic: Topic) {
        AsyncTask.execute({
            run { dataBase!!.topicDAO().updateTopic(topic) }
        })
    }

    private fun addTopicDB(newTopic: Topic) {
        AsyncTask.execute({
            run { dataBase!!.topicDAO().insertTopic(newTopic) }
        })
    }

    private fun deleteTopicDB(deletedTopic: Topic) {
        AsyncTask.execute({
            run { dataBase!!.topicDAO().deleteTopic(deletedTopic) }
        })
    }
}