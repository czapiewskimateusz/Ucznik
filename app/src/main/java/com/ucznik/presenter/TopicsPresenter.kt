package com.ucznik.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.ucznik.model.AppDatabase
import com.ucznik.model.entities.Topic
import com.ucznik.presenter.adapters.TopicsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.activities.LoginActivity
import com.ucznik.view.activities.QuestionsActivity
import com.ucznik.view.dialogs.RenameDialog
import com.ucznik.view.interfaces.ITopicsView

/**
 * Created by Mateusz Czapiewski on 23.02.2018.
 */
const val TOPIC_ID_EXTRA = "TOPIC_ID_EXTRA"

class TopicsPresenter(val view: ITopicsView,
                      private val context: Context,
                      private val activity: FragmentActivity) : TopicsAdapter.TopicsAdapterCallback {

    private var topics = ArrayList<Topic>()
    var topicsAdapter = TopicsAdapter(topics, context, this)


    fun loadData() {
        DatabaseGetTopics(this).execute()
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

    override fun showRenameDialog(position: Int) {
        val renameDialog = RenameDialog()
        renameDialog.currName = topics[position].name
        renameDialog.position = position
        if (context is RenameDialog.RenameDialogListener) renameDialog.renameDialogListener = context
        renameDialog.show(activity.fragmentManager, "rename_dialog")
    }

    fun renameTopic(renameDialog: RenameDialog) {
        val topicName = renameDialog.newName.toString()
        if (renameDialog.position != null) {
            topics[renameDialog.position!!].name = topicName
            topicsAdapter.notifyItemChanged(renameDialog.position!!)
            updateTopicDB(topics[renameDialog.position!!])
        } else addNewTopic(topicName)
    }

    private fun addNewTopic(topicName: String) {
        val newTopic = Topic(name = topicName, done = 0)
        topics.add(newTopic)
        topicsAdapter.notifyItemInserted(topics.size - 1)
        insertTopicDB(newTopic, topics)
    }

    override fun deleteTopic(position: Int) {
        val deletedTopic = topics[position]
        topics.removeAt(position)
        topicsAdapter.notifyItemRemoved(position)
        topicsAdapter.notifyItemRangeChanged(position, topics.size)
        deleteTopicDB(deletedTopic)
    }

    override fun startQuestionsActivity(topicId: Long) {
        val intent = Intent(context, QuestionsActivity::class.java)
        intent.putExtra(TOPIC_ID_EXTRA, topicId)
        context.startActivity(intent)
    }

    private fun updateTopicDB(topic: Topic) {
        AsyncTask.execute {
            run { AppDatabase.getInstance(context)!!.topicDAO().updateTopic(topic) }
        }
    }

    private fun insertTopicDB(newTopic: Topic, topics: ArrayList<Topic>) {
        AsyncTask.execute {
            run {
                newTopic.topicId = AppDatabase.getInstance(context)!!.topicDAO().insertTopic(newTopic)
                val topic = topics.find { it.name == newTopic.name }
                topic?.topicId = newTopic.topicId
            }
        }
    }

    private fun deleteTopicDB(deletedTopic: Topic) {
        AsyncTask.execute {
            run { AppDatabase.getInstance(context)!!.topicDAO().deleteTopic(deletedTopic) }
        }
    }

    companion object {
        class DatabaseGetTopics(private val topicsPresenter: TopicsPresenter) : AsyncTask<Unit, Int, List<Topic>>() {
            override fun doInBackground(vararg p0: Unit?): List<Topic> {
                return AppDatabase.getInstance(topicsPresenter.context)?.topicDAO()?.getAllTopics()!!
            }

            override fun onPostExecute(result: List<Topic>?) {
                topicsPresenter.topics.clear()
                topicsPresenter.topics.addAll(result!!)
                topicsPresenter.topicsAdapter.notifyDataSetChanged()
            }
        }
    }
}