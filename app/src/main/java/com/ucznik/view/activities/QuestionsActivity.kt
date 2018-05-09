package com.ucznik.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ucznik.presenter.QuestionsPresenter
import com.ucznik.presenter.TOPIC_ID_EXTRA
import com.ucznik.ucznik.R
import com.ucznik.view.dialogs.QuestionEditDialog
import com.ucznik.view.interfaces.IQuestionsView
import kotlinx.android.synthetic.main.activity_questions.*

class QuestionsActivity : AppCompatActivity(), IQuestionsView, QuestionEditDialog.QuestionEditDialogListener {

    private val questionPresenter = QuestionsPresenter(this, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        initRV()
        initFAB()
        questionPresenter.loadData(intent.getLongExtra(TOPIC_ID_EXTRA, -1))
    }

    override fun onRestart() {
        questionPresenter.loadData(intent.getLongExtra(TOPIC_ID_EXTRA, -1))
        super.onRestart()
    }

    private fun initFAB() {
        questionsFAB.setOnClickListener {
            questionPresenter.showDialog(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.topic_detail_menu, menu)
        val searchItem = menu?.findItem(R.id.search_question_menu)
        val searchView = searchItem?.actionView as SearchView
        initSearchMenu(searchView)
        val leanItem = menu.findItem(R.id.learn_menu)
        initLearnMenu(leanItem)
        return true
    }

    private fun initLearnMenu(leanItem: MenuItem) {
        leanItem.setOnMenuItemClickListener {
            questionPresenter.startLearning()
        }
    }

    private fun initSearchMenu(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                questionPresenter.onQueryTextChange(newText)
                return true
            }
        })
    }

    override fun updateQuestionStatus(status: String) {
        questionStatusTV.text = status
    }

    override fun setStatusColor(color: Int) {
        questionStatusTV.setTextColor(color)
    }

    override fun onDialogPositiveClick(questionEditDialog: QuestionEditDialog) {
        questionPresenter.updateQuestionDB(questionEditDialog)
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
        }, 300)
    }

    override fun alreadyLearned() {
        val snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), this.getText(R.string.already_learned), Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        snackbar.show()
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
                questionPresenter.removeQuestion(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(questionsRV)
    }

    private fun addOnScrollListener() {
        questionsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) questionsFAB.hide()
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) questionsFAB.show()
            }
        })
    }
}
