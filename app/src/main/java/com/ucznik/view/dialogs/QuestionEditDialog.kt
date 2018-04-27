package com.ucznik.view.dialogs

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ucznik.model.entities.Question
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.question_dialog.*
import kotlinx.android.synthetic.main.question_dialog.view.*
import java.io.IOException
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.view.View.GONE


/**
 * Created by Mateusz on 27.02.2018.
 */
class QuestionEditDialog : DialogFragment() {

    private var questionEditDialogListener: QuestionEditDialogListener? = null
    var question: String? = null
    var answer: String? = null
    var oldQuestion: Question? = null
    var bitmap: Bitmap? = null
    private val resultLoadImage = 1

    interface QuestionEditDialogListener {
        fun onDialogPositiveClick(questionEditDialog: QuestionEditDialog)
        fun onDialogNegativeClick(questionEditDialog: QuestionEditDialog)
        fun showIncompleteDataToast()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.question_dialog, container, false)!!
        initButtons(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillWithData()
    }

    private fun fillWithData() {
        questionEt?.setText(oldQuestion?.question)
        answerEt?.setText(oldQuestion?.answer)
        if (oldQuestion?.image != null) loadImage(oldQuestion?.image!!)
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
        view.addImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Wybierz obrazek"), resultLoadImage)
        }
        view.photoAnswer.setOnClickListener {
            bitmap = null
            photoAnswer.visibility = GONE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == resultLoadImage && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                loadImage(bitmap!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadImage(bitmap: Bitmap) {
        val options = RequestOptions()
        options.fitCenter()
        photoAnswer.visibility = View.VISIBLE
        Glide.with(context!!).load(bitmap).apply(options).into(photoAnswer)
    }

    private fun loadImage(path: String) {
        val options = RequestOptions()
        options.fitCenter()
        photoAnswer.visibility = View.VISIBLE
        Glide.with(context!!).load(path).apply(options).into(photoAnswer)
    }

}