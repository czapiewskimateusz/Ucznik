package com.ucznik.view.activities


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.ucznik.presenter.QuestionsPresenter
import com.ucznik.presenter.TOPIC_ID_EXTRA
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView
import kotlinx.android.synthetic.main.activity_topic_detail.*

class QuestionsActivity : AppCompatActivity(), IQuestionsView, QuestionEditDialog.QuestionEditDialogListener {

    private val questionPresenter = QuestionsPresenter(this, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        questionPresenter.topicId = intent.getLongExtra(TOPIC_ID_EXTRA,-1)
        initRV()
        initFAB()
        questionPresenter.loadData()
    }

    override fun onDestroy() {
        questionPresenter.onDestroy()
        super.onDestroy()
    }

    private fun initFAB() {
        questionsFAB.setOnClickListener {
            questionPresenter.showDialog(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topic_detail_menu, menu)
        return true
    }

    override fun updateQuestionStatus(status: String) {
        questionStatusTV.text = status
    }

    override fun setStatusColor(color: Int) {
        questionStatusTV.setTextColor(color)
    }

    override fun onDialogPositiveClick(questionEditDialog: QuestionEditDialog) {
        questionPresenter.updateQuestion(questionEditDialog)
    }

    override fun onDialogNegativeClick(questionEditDialog: QuestionEditDialog) {
        questionPresenter.hideDialog(questionEditDialog)
    }

    override fun showIncompleteDataToast() {
        Toast.makeText(this, R.string.empty_data_error, Toast.LENGTH_LONG).show()
    }

    override fun scrollToPosition(i: Int) {
        val handler = Handler()
        handler.postDelayed({
            questionsRV.smoothScrollToPosition(i)
        },300)
    }

    private fun initRV() {
        questionsRV.layoutManager = LinearLayoutManager(this)
        questionsRV.setHasFixedSize(true)
        questionsRV.adapter = questionPresenter.questionsAdapter
        addOnScrollListener()
        addOnSwipeListener()
    }

    private fun addOnSwipeListener() {
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                questionPresenter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(questionsRV)
    }

    private fun addOnScrollListener() {
        questionsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && questionsFAB.visibility == View.VISIBLE) questionsFAB.hide()
                else if (dy < 0 && questionsFAB.visibility != View.VISIBLE) questionsFAB.show()
            }
        })
    }
}
