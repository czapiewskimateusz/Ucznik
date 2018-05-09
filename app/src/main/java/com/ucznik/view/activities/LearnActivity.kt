package com.ucznik.view.activities


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
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
import com.github.chrisbanes.photoview.PhotoView


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
            learnPresenter.doNotKnowAnswer()
        })
        btn_yes.setOnClickListener({
            Glide.with(this).clear(photoAnswer)
            learnPresenter.knowAnswer(btn_yes.text.toString())
            questionMark.visibility = VISIBLE
            learnAnswer.visibility = GONE
        })
        photoAnswer.setOnClickListener({
            val mDialog = buildPreviewDialog()
            mDialog.show()
        })
    }

    private fun buildPreviewDialog(): AlertDialog {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.dialog_preview_image, null)
        val imagePreview: PhotoView = mView.findViewById(R.id.imagePreview)
        imagePreview.setImageDrawable(photoAnswer.drawable)
        mBuilder.setView(mView)
        return mBuilder.create()
    }

    override fun updateStatus(status: String) {
        learnStatus.text = status
    }

    override fun learningDone() {
        btn_yes.isEnabled = false
        btn_no.isEnabled = false
        learnQuestion.text = resources.getText(R.string.done_learning)
    }

    override fun displayQuestion(question: Question) {
        setTextSize(question.question)
        learnQuestion.text = question.question
        learnAnswer.text = question.answer
        setType(question)
        learnQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
        btn_no.isEnabled = true
        btn_yes.text = resources.getText(R.string.know_question)
    }

    private fun setType(question: Question) {
        questionType = if (question.image != null) {
            loadImage(question.image!!)
            1
        } else {
            0
        }
    }

    private fun setTextSize(question: String) {
        learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f)
        when {
            question.length < 200 -> learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f)
            question.length < 600 -> learnAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
        }
    }

    override fun showAnswer() {
        questionMark.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
        learnAnswer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        if (questionType == 1) {
            photoAnswer.visibility = VISIBLE
            photoAnswer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        }
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

    private fun loadImage(path: String) {
        val options = RequestOptions()
        options.fitCenter()
        Glide.with(this).load(path).apply(options).into(photoAnswer)
    }
}
