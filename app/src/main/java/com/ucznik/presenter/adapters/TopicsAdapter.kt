package com.ucznik.presenter.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.ucznik.model.entities.Topic
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.topic_item.view.*

/**
 * Created by Mateusz on 22.02.2018.
 */
class TopicsAdapter(private var topics: ArrayList<Topic>,
                    private val context: Context,
                    private val topicsAdapterCallback: TopicsAdapterCallback) : RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>() {

    interface TopicsAdapterCallback {
        fun deleteTopic(position: Int)
        fun showRenameDialog(position: Int)
        fun markDone(position: Int)
        fun markUndone(position: Int)
        fun showQuestions(topicId: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TopicViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.topic_item, parent, false)
        return TopicViewHolder(v)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder?, position: Int) {
        holder?.topicName?.text = topics[position].name
        setDoneMark(position, holder)
        setClickListener(holder, position)
    }

    /**
     * Sets a DONE mark based on topics done parameter
     */
    private fun setDoneMark(position: Int, holder: TopicViewHolder?) {
        if (topics[position].done==1) holder?.doneIV?.visibility = VISIBLE
        else holder?.doneIV?.visibility = INVISIBLE
    }

    /**
     * Sets onClickListener to display popup menu
     */
    private fun setClickListener(holder: TopicViewHolder?, position: Int) {
        holder?.topicOptionsIV?.setOnClickListener({
            val popupMenu = PopupMenu(context, holder.topicOptionsIV)
            popupMenu.inflate(R.menu.topic_detail)
            setItemClickListener(popupMenu, position)
            popupMenu.show()
        })

        holder?.container?.setOnClickListener({
           topicsAdapterCallback.showQuestions(topics[position].topicId)
        })
    }

    /**
     * Sets onItemClickListener to handle menu operations
     */
    private fun setItemClickListener(popupMenu: PopupMenu, position: Int) {
        popupMenu.setOnMenuItemClickListener({
            when (it.itemId) {
                R.id.delete_topic -> {
                    removeTopic(position)
                    return@setOnMenuItemClickListener true
                }
                R.id.rename_topic -> {
                    renameTopic(position)
                    return@setOnMenuItemClickListener true
                }
                R.id.mark_done -> {
                    markDone(position)
                    return@setOnMenuItemClickListener true
                }
                R.id.mark_undone -> {
                    markUndone(position)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        })
    }

    private fun renameTopic(position: Int) {
       topicsAdapterCallback.showRenameDialog(position)
    }

    private fun markUndone(position: Int) {
        topicsAdapterCallback.markUndone(position)
    }

    private fun markDone(position: Int) {
       topicsAdapterCallback.markDone(position)
    }

    private fun removeTopic(position: Int) {
       topicsAdapterCallback.deleteTopic(position)
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicName: TextView = itemView.topicName
        val container: CardView = itemView.topicContainer
        val doneIV: ImageView = itemView.doneIV
        val topicOptionsIV: ImageView = itemView.topicOptionsIV
    }
}