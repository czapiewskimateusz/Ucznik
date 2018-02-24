package com.ucznik.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.ucznik.model.db.AppDatabase
import com.ucznik.model.entities.Topic
import com.ucznik.presenter.adapters.TopicsAdapter
import com.ucznik.ucznik.R
import com.ucznik.view.LoginActivity
import com.ucznik.view.RenameDialog
import com.ucznik.view.interfaces.ITopicsView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Mateusz on 23.02.2018.
 */
class TopicsPresenter(val view: ITopicsView,
                      private val context: Context,
                      private val activity: FragmentActivity) : TopicsAdapter.TopicsAdapterCallback {

    private val compositeDisposable = CompositeDisposable()
    private var topics = ArrayList<Topic>()
    var topicsAdapter: TopicsAdapter? = TopicsAdapter(topics, context, this)
    private var dataBase: AppDatabase? = null

    fun loadData() {
        dataBase = AppDatabase.getInstance(context)
        fetchTopicDataFromDB()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        AppDatabase.destroyInstance()
    }

    fun logout() {
        Toast.makeText(context, "WYLOGOWANIE", Toast.LENGTH_SHORT).show()
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
        topicsAdapter?.notifyItemChanged(position)
        compositeDisposable.add(Observable.fromCallable { dataBase!!.topicDAO().updateTopic(topics[position]) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun markUndone(position: Int) {
        topics[position].done = 0
        topicsAdapter?.notifyItemChanged(position)
        compositeDisposable.add(Observable.fromCallable { dataBase!!.topicDAO().updateTopic(topics[position]) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun fetchTopicDataFromDB() {
        compositeDisposable.add(dataBase!!.topicDAO().getAllTopics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    topics.clear()
                    topics.addAll(it)
                    topicsAdapter?.notifyDataSetChanged()
                }))
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

    private fun updateTopicDB(topic: Topic) {
        compositeDisposable.add(Observable.fromCallable { dataBase!!.topicDAO().updateTopic(topic) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun addNewTopic(topicName: String) {
        val newTopic = Topic(name = topicName, done = 0)
        compositeDisposable.add(Observable.fromCallable { dataBase!!.topicDAO().insertTopic(newTopic) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun removeTopic(position: Int) {
        val deletedTopic = topics[position]
        topicsAdapter?.notifyItemRemoved(position)
        topicsAdapter?.notifyItemRangeChanged(position, topics.size)
        compositeDisposable.add(Observable.fromCallable { dataBase!!.topicDAO().deleteTopic(deletedTopic) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }


}