package com.ucznik.view.activities


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View.*
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ucznik.model.entities.Question
import com.ucznik.presenter.LearnPresenter
import com.ucznik.presenter.TOPIC_ID_EXTRA
import com.ucznik.ucznik.R
import com.ucznik.view.interfaces.ILearnView
import kotlinx.android.synthetic.main.activity_learn.*

class LearnActivity : AppCompatActivity(), ILearnView {

    private val learnPresenter = LearnPresenter(this, this)
    private var questionType = 0

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
            Glide.with(this).clear(photoAnswer)
            learnPresenter.know(btn_yes.text.toString())
            questionMark.visibility = VISIBLE
            learnAnswer.visibility = GONE
        })
    }

    override fun updateStatus(status: String) {
        learnStatus.text = status
    }

    override fun learningDone() {
        learnAnswer.visibility = GONE
        photoAnswer.visibility = GONE
        questionMark.visibility = INVISIBLE
        btn_yes.isEnabled = false
        btn_no.isEnabled = false
        learnQuestion.text = resources.getText(R.string.done_learning)
    }

    override fun displayQuestion(question: Question) {
        setTextSize(question.question)
        learnQuestion.text = question.question
        learnAnswer.text = question.answer
        questionType = if (question.image != null) {
            loadImage(question.image!!)
            1
        } else {
            0
        }
        learnQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
        btn_no.isEnabled = true
        btn_yes.text = resources.getText(R.string.know_question)
    }

    private fun setTextSize(question: String) {
        learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f)
        when {
            question.length < 200 -> learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f)
            question.length < 600 -> learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
        }
    }

    override fun showAnswer() {
        if (questionType == 1) photoAnswer.visibility = VISIBLE
        questionMark.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
        learnAnswer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        photoAnswer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        questionMark.visibility = INVISIBLE
        learnAnswer.visibility = VISIBLE
        btn_no.isEnabled = false
        btn_yes.text = "OK"
    }

    override fun hideAnswer() {
        photoAnswer.visibility = GONE
        learnAnswer.visibility = GONE
        questionMark.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        questionMark.visibility = VISIBLE
    }

    private fun loadImage(path:String) {
        val options = RequestOptions()
        options.fitCenter()
        Glide.with(this).load(path).apply(options).into(photoAnswer)
    }
}
