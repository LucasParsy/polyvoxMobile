package com.tuxlu.polyvox.Utils

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.TextView
import com.tuxlu.polyvox.R

/**
 * Created by tuxlu on 29/12/17.
 */
object InputFieldsVerifier {

    @JvmStatic private fun hideTextView(view: TextInputLayout) {
        for (i in 0..view.childCount) {
            val child = view.getChildAt(i)
            if (child is TextView) {
                child.visibility = View.GONE
                break
            }
        }
    }

    @JvmStatic
    fun checkPassword(view: TextInputLayout, context: Context): Boolean {
        val str: String = view.editText?.text.toString()
        if (UtilsTemp.isStringEmpty(str) || !str.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,128}$".toRegex())) {
            view.error = context.getString(R.string.register_password_error)
            hideTextView(view)
            return false
        }
        return true
    }

    @JvmStatic
    fun checkMail(view: TextInputLayout, context: Context): Boolean {
        val str: String = view.editText?.text.toString()
        if (UtilsTemp.isStringEmpty(str) || !str.contains("@")) {
            view.error = context.getString(R.string.register_mail_error)
            hideTextView(view)
            return false
        }
        return true
    }
}