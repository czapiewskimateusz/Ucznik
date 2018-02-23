package com.ucznik.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.ucznik.ucznik.R
import com.ucznik.view.interfaces.ILoginView
import com.ucznik.view.RegisterActivity
import com.ucznik.view.TopicsActivity


/**
 * Created by Mateusz on 21.02.2018.
 */
class LoginPresenter(private val view: ILoginView, private val context: Context) {

    fun login(email: String, password: String) {
        //view.showProgress(true)
        if (verifyCredentials(email, password)) {
            view.onLoginError("JEST OK")
            saveUsersId(1)
            startTopicsActivity()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveUsersId(id: Int) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(context.getString(R.string.saved_user_id), id)
            apply()
        }
    }

    private fun verifyCredentials(email: String, password: String): Boolean {
        var check = true
        view.clearErrors()

        if (TextUtils.isEmpty(email)) {
            view.onEmailError(context.getString(R.string.empty_email_error))
            check = false
        } else if (!email.contains("@")) {
            view.onEmailError(context.getString(R.string.not_email_error))
            check = false
        }

        if (TextUtils.isEmpty(password)) {
            view.onPasswordError(context.getString(R.string.empty_password_error))
            check = false
        }

        return check
    }

    fun startRegisterActivity() {
        val intent = Intent(context, RegisterActivity::class.java)
        context.startActivity(intent)
    }

    fun checkIfLoggedIn() {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        if (sharedPref.getInt(context.getString(R.string.saved_user_id), 0) > 0) {
            startTopicsActivity()
        }
    }

    private fun startTopicsActivity() {
        val intent = Intent(context, TopicsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}