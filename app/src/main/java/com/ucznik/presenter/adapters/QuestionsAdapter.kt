package com.ucznik.presenter.adapters

import android.content.Context
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ucznik.model.entities.Question
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.question_item.view.*

class QuestionsAdapter(private val questions: ArrayList<Question>,
                       private val context: Context,
                       private val questionsAdapterListener: QuestionsAdapterListener) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    private val sortedList = SortedList<Question>(Question::class.java, object : SortedList.Callback<Question>() {

        override fun areItemsTheSame(item1: Question?, item2: Question?): Boolean {
            return item1?.questionId == item2?.questionId
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun compare(o1: Question?, o2: Question?): Int {
            return (o1?.questionId!! - o2?.questionId!!).toInt()
        }

        override fun areContentsTheSame(oldItem: Question?, newItem: Question?): Boolean {
            return oldItem?.question?.equals(newItem?.question)!!
        }
    })

    interface QuestionsAdapterListener {
        fun updateStatus(position: Int)
        fun updateStatus(id: Long)
        fun questionClicked(question: Question)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): QuestionViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.question_item, parent, false)
        return QuestionViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    override fun onBindViewHolder(holder: QuestionViewHolder?, position: Int) {
        holder?.questionTV?.text = sortedList[position].question
        holder?.id = sortedList[position].questionId
        setDoneMark(position, holder)
    }

    fun replaceAll(filteredModeList: List<Question>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val question = sortedList[i]
            if (!filteredModeList.contains(question)) sortedList.remove(question)
        }
        sortedList.addAll(filteredModeList)
        sortedList.endBatchedUpdates()
    }

    fun addAll(questions: List<Question>) {
        sortedList.addAll(questions)
    }

    fun remove(question: Question) {
        sortedList.remove(question)
    }

    fun add(question: Question) {
        sortedList.add(question)
    }

    fun update(question: Question) {
        for (i in 0 until sortedList.size()) {
            if (sortedList[i].questionId == question.questionId) {
                sortedList[i].answer = question.answer
                sortedList[i].question = question.question
                notifyItemChanged(i)
                break
            }
        }
    }

    fun getQuestion(adapterPosition: Int): Question {
        return sortedList[adapterPosition]
    }

    /**
     * Sets a DONE mark based on topics done parameter
     */
    private fun setDoneMark(position: Int, holder: QuestionViewHolder?) {
        if (sortedList[position].done == 1) holder?.questionDoneIV?.setImageDrawable(context.getDrawable(R.drawable.ic_done))
        else holder?.questionDoneIV?.setImageDrawable(context.getDrawable(R.drawable.ic_done_empty))
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTV: TextView = itemView.questionTV
        val questionDoneIV: ImageView = itemView.questionDoneIV
        var id: Long? = null

        init {
            questionDoneIV.setOnClickListener({
                val question = getQuestionById(id!!)
                if (question.done == 1) {
                    markUndone(question)
                } else {
                    markDone(question)
                }
            })

            itemView.setOnClickListener({
                questionsAdapterListener.questionClicked(sortedList[adapterPosition])
            })
        }

        private fun markUndone(question: Question) {
            questionDoneIV.setImageDrawable(context.getDrawable(R.drawable.ic_done_empty))
            question.done = 0
            questionsAdapterListener.updateStatus(id!!)
        }

        private fun markDone(question: Question) {
            questionDoneIV.setImageDrawable(context.getDrawable(R.drawable.ic_done))
            question.done = 1
            questionsAdapterListener.updateStatus(id!!)
        }

        private fun getQuestionById(id: Long): Question {
            var question: Question? = null
            questions.forEach({
                if (it.questionId == id) {
                    question = it
                }
            })
            return question!!
        }

    }
}