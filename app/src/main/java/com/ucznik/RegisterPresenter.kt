package com.ucznik

import android.content.Context
import android.text.TextUtils
import com.ucznik.ucznik.R

/**
 * Created by Mateusz on 21.02.2018.
 */
class RegisterPresenter(private val view: IRegisterView, val context: Context) {

    fun register(email: String, password: String, passwordRepeat: String) {
        //view.showProgress(true)
        if (verifyCredentials(email,password,passwordRepeat)){
            view.onRegisterError("Wszystko jest luks")
        }
    }

    private fun verifyCredentials(email: String, password: String, passwordRepeat: String): Boolean {
        var check = true
        view.clearErrors()

        if (TextUtils.isEmpty(email)){
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

        if (TextUtils.isEmpty(passwordRepeat)){
            view.onPasswordRepeatError(context.getString(R.string.empty_password_error))
            check = false
        } else if (password!=passwordRepeat){
            view.onPasswordRepeatError(context.getString(R.string.passwords_not_match_error))
            check = false
        }

        return check
    }
}