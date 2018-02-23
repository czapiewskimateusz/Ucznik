package com.ucznik.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ucznik.ucznik.R
import com.ucznik.view.LoginActivity
import com.ucznik.view.interfaces.ITopicsView

/**
 * Created by Mateusz on 23.02.2018.
 */
class TopicsPresenter(val view: ITopicsView, private val context: Context) {

    fun logout() {
        Toast.makeText(context, "WYLOGOWANIE", Toast.LENGTH_SHORT).show()
        deleteUsersId()
        startLoginActivity()
    }

    @SuppressLint("CommitPrefEdits")
    private fun deleteUsersId() {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(context.getString(R.string.saved_user_id), -1)
            apply()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}