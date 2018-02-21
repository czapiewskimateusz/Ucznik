package com.ucznik

/**
 * Created by Mateusz on 21.02.2018.
 */
interface IRegisterView {
    fun showProgress(show: Boolean)
    fun onEmailError(error: String)
    fun onPasswordError(error: String)
    fun onPasswordRepeatError(error: String)
    fun onRegisterError(error: String)
    fun clearErrors()
}