package com.ucznik.view

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.ucznik.model.Topic
import com.ucznik.model.TopicsAdapter
import com.ucznik.presenter.TopicsPresenter
import com.ucznik.ucznik.R
import com.ucznik.view.interfaces.ITopicsView
import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : AppCompatActivity(), RenameDialog.RenameDialogListener, ITopicsView {

    private val topicPresenter = TopicsPresenter(this, this)
    private var topics = Topic.getSampleData()
    private var topicsAdapter: TopicsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        initRV()
        initFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topics_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.logout_menu -> topicPresenter.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogPositiveClick(renameDialog: RenameDialog) {
        val topicName = renameDialog.newName.toString()
        if (renameDialog.position != null) topics[renameDialog.position!!].name = topicName
        else {
            addNewTopic(topicName)
        }
        topicsAdapter?.notifyDataSetChanged()
    }

    private fun addNewTopic(topicName: String) {
        topics.add(Topic(412, topicName, false))
        Handler().postDelayed({
            topicsRV.smoothScrollToPosition(topics.size)
        }, 350)
    }

    private fun initFAB() {
        topicsFAB.setOnClickListener({
            val renameDialog = RenameDialog()
            renameDialog.show(this.fragmentManager, "rename_dialog")
        })
    }

    private fun initRV() {
        topicsRV.layoutManager = GridLayoutManager(this, 2)
        topicsRV.setHasFixedSize(true)
        topicsAdapter = TopicsAdapter(topics, this, this)
        topicsRV.adapter = topicsAdapter
        addOnScrollListener()
    }

    private fun addOnScrollListener() {
        topicsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> topicsFAB.show()
                    else -> topicsFAB.hide()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }
}
