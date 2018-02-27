package com.ucznik.view

import android.os.Bundle

import android.support.v7.app.AppCompatActivity

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ucznik.presenter.TopicsPresenter
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.RenameDialog
import com.ucznik.view.interfaces.ITopicsView
import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : AppCompatActivity(), RenameDialog.RenameDialogListener, ITopicsView {

    private val topicPresenter = TopicsPresenter(this, this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        initRV()
        initFAB()
        topicPresenter.loadData()
    }

    override fun onDestroy() {
        topicPresenter.onDestroy()
        super.onDestroy()
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
       topicPresenter.renameTopic(renameDialog)
    }

    override fun scrollToPosition(position: Int) {
        topicsRV.smoothScrollToPosition(position)
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
        topicsRV.adapter = topicPresenter.topicsAdapter
        addOnScrollListener()
    }

    private fun addOnScrollListener() {
        topicsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && topicsFAB.visibility == View.VISIBLE) topicsFAB.hide()
                else if (dy < 0 && topicsFAB.visibility != View.VISIBLE) topicsFAB.show()
            }
        })
    }
}
