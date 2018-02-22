package com.ucznik.view.interfaces

/**
 * Created by Mateusz on 21.02.2018.
 */
interface ILoginView {

    fun showProgress(show: Boolean)
    fun onEmailError(error: String)
    fun onPasswordError(error: String)
    fun onLoginError(error: String)
    fun clearErrors()
}