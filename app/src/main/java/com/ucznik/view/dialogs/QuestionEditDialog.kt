package com.ucznik.view.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ucznik.model.entities.Question
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.question_dialog.*
import kotlinx.android.synthetic.main.question_dialog.view.*

/**
 * Created by Mateusz on 27.02.2018.
 */
class QuestionEditDialog : DialogFragment() {

    private var questionEditDialogListener: QuestionEditDialogListener? = null
    var question: String? = null
    var answer: String? = null
    var oldQuestion: Question? = null

    interface QuestionEditDialogListener {
        fun onDialogPositiveClick(questionEditDialog: QuestionEditDialog)
        fun onDialogNegativeClick(questionEditDialog: QuestionEditDialog)
        fun showIncompleteDataToast()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.question_dialog, container, false)!!
        initButtons(view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionEt?.setText(oldQuestion?.question)
        answerEt?.setText(oldQuestion?.answer)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is QuestionEditDialogListener) questionEditDialogListener = context
    }

    private fun initButtons(view: View) {
        view.cancelBtn.setOnClickListener {
            questionEditDialogListener?.onDialogNegativeClick(this)
        }
        view.saveBtn.setOnClickListener {
            question = questionEt?.text?.toString()
            answer = answerEt?.text?.toString()
            if (question?.length == 0 || answer?.length == 0) questionEditDialogListener?.showIncompleteDataToast()
            else questionEditDialogListener?.onDialogPositiveClick(this)
        }
    }
}