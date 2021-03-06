package com.ucznik.view.activities

import android.os.Bundle

import android.support.v7.app.AppCompatActivity

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.ucznik.presenter.TopicsPresenter
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.RenameDialog
import com.ucznik.view.interfaces.ITopicsView
import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : AppCompatActivity(), RenameDialog.RenameDialogListener, ITopicsView {

    private val topicPresenter = TopicsPresenter(this, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        initRV()
        initFAB()
        topicPresenter.loadData()
    }

    override fun onRestart() {
        topicPresenter.loadData()
        super.onRestart()
    }

    override fun onDialogPositiveClick(renameDialog: RenameDialog) {
        topicPresenter.renameTopic(renameDialog)
    }

    override fun scrollToPosition(position: Int) {
        topicsRV.smoothScrollToPosition(position)
    }

    private fun initFAB() {
        topicsFAB.setOnClickListener {
            val renameDialog = RenameDialog()
            renameDialog.renameDialogListener = this
            renameDialog.show(this.fragmentManager, "rename_dialog")
        }
    }

    private fun initRV() {
        topicsRV.layoutManager = GridLayoutManager(this, 2)
        //topicsRV.setHasFixedSize(true)
        topicsRV.adapter = topicPresenter.topicsAdapter
        addOnScrollListener()
    }

    private fun addOnScrollListener() {
        topicsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) topicsFAB.hide()
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) topicsFAB.show()
            }

        })
    }
}
