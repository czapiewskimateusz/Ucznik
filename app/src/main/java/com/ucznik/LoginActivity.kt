package com.ucznik

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.ucznik.ucznik.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), ILoginView {

    private val loginPresenter = LoginPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initListeners()
        setKeyboardListener()
    }

    override fun showProgress(show: Boolean) {
        if (show) loginProgressBar?.visibility = VISIBLE
        else loginProgressBar?.visibility = INVISIBLE
    }

    override fun onEmailError(error: String) {
        inputLoginLayoutEmail.error = error
    }

    override fun onPasswordError(error: String) {
        inputLoginLayoutPassword.error = error
    }

    override fun onLoginError(error: String) {
        loginGeneralError.text = error
    }

    override fun clearErrors() {
        inputLoginLayoutEmail.error = ""
        inputLoginLayoutPassword.error = ""
        loginGeneralError.text = ""
    }

    private fun initListeners() {
        login_btn.setOnClickListener({
           login()
        })

        register_hint?.setOnClickListener({
            loginPresenter.startRegisterActivity()
        })
    }

    private fun setKeyboardListener() {
        inputLoginPassword.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                login()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun login() {
        loginPresenter.login(inputLoginEmail?.text.toString(), inputLoginPassword?.text.toString())
    }
}
