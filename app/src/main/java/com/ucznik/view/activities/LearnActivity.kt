package com.ucznik.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.ucznik.model.entities.Question
import com.ucznik.presenter.LearnPresenter
import com.ucznik.presenter.TOPIC_ID_EXTRA
import com.ucznik.ucznik.R
import com.ucznik.view.interfaces.ILearnView
import kotlinx.android.synthetic.main.activity_learn.*

class LearnActivity : AppCompatActivity(), ILearnView {

    private val learnPresenter = LearnPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        initButtons()
        learnPresenter.loadQuestions(intent.getLongExtra(TOPIC_ID_EXTRA, -1))
    }

    private fun initButtons() {
        btn_no.setOnClickListener({
            learnPresenter.doNotKnow()
        })
        btn_yes.setOnClickListener({
            learnPresenter.know(btn_yes.text.toString())
            questionMark.visibility = VISIBLE
            learnAnswer.visibility = INVISIBLE
        })
    }

    override fun updateStatus(status: String) {
        learnStatus.text = status
    }

    override fun learningDone() {
        questionMark.visibility = INVISIBLE
        learnAnswer.visibility = INVISIBLE
        btn_yes.isEnabled = false
        btn_no.isEnabled = false
        learnQuestion.text = resources.getText(R.string.done_learning)
    }

    override fun displayQuestion(question: Question) {
        learnQuestion.text = question.question
        learnAnswer.text = question.answer
        btn_no.isEnabled = true
        btn_yes.text = resources.getText(R.string.know_question)
    }

    override fun showAnswer() {
        questionMark.visibility = INVISIBLE
        learnAnswer.visibility = VISIBLE
        btn_no.isEnabled = false
        btn_yes.text = "OK"
    }
}
