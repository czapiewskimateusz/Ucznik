package com.ucznik.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ucznik.model.Topic
import com.ucznik.model.TopicsAdapter
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        initRV()
        initFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topics_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.logout_menu -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        Toast.makeText(this,"WYLOGOWANIE",Toast.LENGTH_SHORT).show()
    }

    private fun initFAB() {
        topicsFAB.setOnClickListener({
            Snackbar.make(it, "Testujemy kliknięcie na FABA", Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun initRV() {
        topicsRV.layoutManager = GridLayoutManager(this, 2)
        topicsRV.setHasFixedSize(true)
        topicsRV.adapter = TopicsAdapter(Topic.getSampleData(), this)
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
