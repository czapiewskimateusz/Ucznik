package com.ucznik.model

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
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
import com.ucznik.ucznik.R
import com.ucznik.view.RenameDialog
import com.ucznik.view.TopicDetailActivity
import kotlinx.android.synthetic.main.topic_item.view.*

/**
 * Created by Mateusz on 22.02.2018.
 */
class TopicsAdapter(private var topics: ArrayList<Topic>,
                    private val context: Context,
                    private val activity: FragmentActivity) : RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>() {

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
        if (topics[position].done) holder?.doneIV?.visibility = VISIBLE
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
            //TODO Pass clicked topic id
            val intent = Intent(context, TopicDetailActivity::class.java)
            context.startActivity(intent)
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
        val renameDialog = RenameDialog()
        renameDialog.currName = topics[position].name
        renameDialog.position = position
        renameDialog.show(activity.fragmentManager, "rename_dialog")
    }

    private fun markUndone(position: Int) {
        topics[position].done = false
        this.notifyDataSetChanged()
    }

    private fun markDone(position: Int) {
        topics[position].done = true
        this.notifyDataSetChanged()
    }

    private fun removeTopic(position: Int) {
        val deletedTopic = topics[position]
        val deletedPosition = position
        topics.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyItemRangeChanged(position, itemCount)

        showSnackBar(deletedTopic, deletedPosition)
    }

    private fun showSnackBar(deletedTopic: Topic, deletedPosition: Int) {
        val snackBar = Snackbar.make(activity.currentFocus, context.getString(R.string.deleted) + " ${deletedTopic.name}", Snackbar.LENGTH_LONG)

        snackBar.setAction(context.getString(R.string.cancel), {
            topics.add(deletedPosition, deletedTopic)
            this@TopicsAdapter.notifyItemInserted(deletedPosition)
            this.notifyItemRangeChanged(deletedPosition, itemCount)
        })
        snackBar.show()

    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicName: TextView = itemView.topicName
        val container: CardView = itemView.topicContainer
        val doneIV: ImageView = itemView.doneIV
        val topicOptionsIV: ImageView = itemView.topicOptionsIV
    }
}