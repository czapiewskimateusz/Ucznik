package com.ucznik

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.ucznik.ucznik.R


/**
 * Created by Mateusz on 21.02.2018.
 */
class LoginPresenter(private val view: ILoginView, private val context: Context) {

    fun login(email: String, password: String) {
        //view.showProgress(true)
        if(verifyCredentials(email,password)){
            view.onLoginError("JEST OK")
        }
    }

    private fun verifyCredentials(email: String, password: String): Boolean {
        var check = true
        view.clearErrors()

        if (TextUtils.isEmpty(email)) {
            view.onEmailError(context.getString(R.string.empty_email_error))
            check = false
        } else if (!email.contains("@")){
            view.onEmailError(context.getString(R.string.not_email_error))
            check = false
        }

        if (TextUtils.isEmpty(password)){
            view.onPasswordError(context.getString(R.string.empty_password_error))
            check = false
        }

        return check
    }

    fun startRegisterActivity() {
        val intent = Intent(context, RegisterActivity::class.java)
        context.startActivity(intent)
    }

}