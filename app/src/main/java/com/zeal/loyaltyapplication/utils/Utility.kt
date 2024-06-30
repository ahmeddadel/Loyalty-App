package com.zeal.loyaltyapplication.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor

/**
 * @created 29/06/2024 - 9:36 PM
 * @project Loyalty Application
 * @author adell
 */
object Utility {
    fun formatAmount(amount: Float): Float =
        BigDecimal(amount.toDouble()).setScale(2, BigDecimal.ROUND_DOWN).toFloat()

    fun showSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}