package com.ucznik.view

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import com.ucznik.model.entities.Question
import com.ucznik.presenter.adapters.QuestionsAdapter
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.activity_topic_detail.*

class TopicDetailActivity : AppCompatActivity(), QuestionsAdapter.MarkedDoneListener {

    private var questions = Question.getSampleData()
    private var questionsAdapter: QuestionsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        changeStatus()
        initRV()
    }

    private fun changeStatus() {
        var done = 0.0
        questions.forEach { q -> if (q.done==1) done++ }
        val percentage: Double = (done / questions.size) * 100
        questionStatusTV.text = String.format("%.2f", percentage) + "%"
        if (percentage<50.0) {
            questionStatusTV.setTextColor(Color.RED)
            return
        }
        if (percentage<=80.0) {
            questionStatusTV.setTextColor(Color.YELLOW)
            return
        } else questionStatusTV.setTextColor(Color.GREEN)
    }

    override fun markedDone() {
        changeStatus()
    }

    override fun markedUndone() {
        changeStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topic_detail_menu, menu)
        return true
    }

    private fun initRV() {
        questionsRV.setHasFixedSize(true)
        questionsRV.layoutManager = LinearLayoutManager(this)
        questionsAdapter = QuestionsAdapter(questions, this, this)
        questionsRV.adapter = questionsAdapter
        addOnScrollListener()
    }

    private fun addOnScrollListener() {
        questionsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                when(newState){
//                    RecyclerView.SCROLL_STATE_IDLE -> questionsFAB.show()
//                    RecyclerView.SCROLL_STATE_DRAGGING -> questionsFAB.hide()
//                }
//            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && questionsFAB.visibility == View.VISIBLE) questionsFAB.hide()
                else if (dy < 0 && questionsFAB.visibility != View.VISIBLE) questionsFAB.show()
            }
        })
    }
}
