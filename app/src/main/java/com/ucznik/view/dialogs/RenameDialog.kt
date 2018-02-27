package com.ucznik.view.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.dialog_topic_rename.view.*

/**
 * Created by Mateusz on 23.02.2018.
 */
class RenameDialog: DialogFragment() {

    private var renameDialogListener: RenameDialogListener? = null
    var newName: String? = null
    var currName: String? = null
    var position: Int? = null

    interface  RenameDialogListener {
        fun onDialogPositiveClick(renameDialog: RenameDialog)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RenameDialogListener) renameDialogListener = context
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_topic_rename,null)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(currName ?: getString(R.string.new_topic))
                .setView(view)
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    newName = view.renameET.text.toString()
                    if (!TextUtils.isEmpty(newName)) renameDialogListener?.onDialogPositiveClick(this)
                }
                .setNegativeButton(getString(R.string.cancel)){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
        return builder.create()
    }

}