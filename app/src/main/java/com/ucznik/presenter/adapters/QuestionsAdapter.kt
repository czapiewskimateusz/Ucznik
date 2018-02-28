package com.ucznik.presenter.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ucznik.model.entities.Question
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.question_item.view.*

/**
 * Created by Mateusz on 22.02.2018.
 */
class QuestionsAdapter(private var questions: ArrayList<Question>,
                       private val context: Context,
                       private val questionsAdapterListener: QuestionsAdapterListener) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    interface QuestionsAdapterListener {
        fun updateStatus(position: Int)
        fun questionClicked(question:Question)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): QuestionViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.question_item, parent, false)
        return QuestionViewHolder(v)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder?, position: Int) {
        holder?.questionTV?.text = questions[position].question
        holder?.position = position
        setDoneMark(position, holder)
    }

    /**
     * Sets a DONE mark based on topics done parameter
     */
    private fun setDoneMark(position: Int, holder: QuestionViewHolder?) {
        if (questions[position].done==1) holder?.questionDoneIV?.setImageDrawable(context.getDrawable(R.drawable.ic_done))
        else holder?.questionDoneIV?.setImageDrawable(context.getDrawable(R.drawable.ic_done_empty))
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val questionTV: TextView = itemView.questionTV
        val questionDoneIV: ImageView = itemView.questionDoneIV
        var position: Int? = null

        init {
            questionDoneIV.setOnClickListener( {
                if (questions[position!!].done==1) {
                    markUndone()
                } else {
                    markDone()
                }
                this@QuestionsAdapter.notifyItemChanged(position!!)
            })

            itemView.setOnClickListener({
                questionsAdapterListener.questionClicked(questions[position!!])
            })
        }

        private fun markUndone() {
            questionDoneIV.setImageDrawable(context.getDrawable(R.drawable.ic_done_empty))
            questions[position!!].done = 0
            questionsAdapterListener.updateStatus(position!!)
        }

        private fun markDone() {
            questionDoneIV.setImageDrawable(context.getDrawable(R.drawable.ic_done))
            questions[position!!].done = 1
            questionsAdapterListener.updateStatus(position!!)
        }

    }
}