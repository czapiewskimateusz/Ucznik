package com.ucznik.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.ucznik.presenter.RegisterPresenter
import com.ucznik.ucznik.R
import com.ucznik.view.interfaces.IRegisterView
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), IRegisterView {

    private val registerPresenter = RegisterPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        initButton()
    }

    override fun showProgress(show: Boolean) {
        if (show) registerPB?.visibility = VISIBLE
        else registerPB?.visibility = INVISIBLE
    }

    override fun onEmailError(error: String) {
        inputRegisterLayoutEmail?.error = error
    }

    override fun onPasswordError(error: String) {
        inputRegisterLayoutPassword?.error = error
    }

    override fun onPasswordRepeatError(error: String) {
        inputRegisterLayoutPasswordRepeat?.error = error
    }

    override fun onRegisterError(error: String) {
        registerError?.text = error
    }

    override fun clearErrors() {
        inputRegisterLayoutEmail?.error = ""
        inputRegisterLayoutPassword?.error = ""
        inputRegisterLayoutPasswordRepeat?.error = ""
        registerError?.text = ""
    }

    private fun initButton() {
        registerBtn.setOnClickListener {
            registerPresenter.register(inputRegisterEmail?.text.toString(),
                    inputRegisterPassword?.text.toString(),
                    inputRegisterPasswordRepeat?.text.toString())
        }
    }
}
