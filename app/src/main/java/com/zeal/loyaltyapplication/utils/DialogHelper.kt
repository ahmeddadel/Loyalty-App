package com.zeal.loyaltyapplication.utils

import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.zeal.loyaltyapplication.LoyaltyApp
import com.zeal.loyaltyapplication.R
import com.zeal.loyaltyapplication.databinding.DiscountAmountDialogBinding
import com.zeal.loyaltyapplication.databinding.LoadingDialogBinding

object DialogHelper {

    private var loadingDialog: Dialog? = null;

    fun showDiscountAmountPanDialog(
        activity: Activity,
        isDiscountPercentage: Boolean,
        sharedPref: SharedPreferences,
        onFinish: () -> Unit
    ) = activity.apply {
        val dialogView = DiscountAmountDialogBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView.root)
            .setTitle(getString(R.string.enter_discount_amount))
            .create()

        dialog.setCanceledOnTouchOutside(false) // Prevent dialog from closing on outside touch
        dialog.setCancelable(false) // Prevent dialog from closing on back press

        dialogView.btnSubmit.setOnClickListener {
            handleSubmit(dialogView, isDiscountPercentage, sharedPref, dialog, onFinish)
        }
        dialogView.etDiscountAmount.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                handleSubmit(dialogView, isDiscountPercentage, sharedPref, dialog, onFinish)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        LoyaltyApp.getInstance().runOnUiThreadWithDelay({
            Utility.showSoftKeyboard(this@apply, dialogView.etDiscountAmount)
        }, 250)
    }

    private fun Activity.handleSubmit(
        dialogView: DiscountAmountDialogBinding,
        isDiscountPercentage: Boolean,
        sharedPref: SharedPreferences,
        dialog: AlertDialog,
        onFinish: () -> Unit
    ) {
        val amount = dialogView.etDiscountAmount.text.toString()
        try {
            if (amount.isNotEmpty() && amount.toFloat() >= 0) {
                if (isDiscountPercentage) {
                    if (amount.toFloat() > 100) {
                        Toast.makeText(
                            this,
                            getString(R.string.please_enter_valid_amount),
                            Toast.LENGTH_LONG
                        ).show();
                        return
                    }
                    LoyaltyApp.getInstance().runOnBackgroundThreadWithCallback({
                        sharedPref.edit().putFloat(
                            Constants.DISCOUNT_PERCENTAGE,
                            Utility.formatAmount(amount.toFloat())
                        ).apply()
                    }, {
                        dialog.dismiss()
                        onFinish()
                    })
                } else {
                    LoyaltyApp.getInstance().runOnBackgroundThreadWithCallback({
                        sharedPref.edit().putFloat(
                            Constants.DISCOUNT_FIXED,
                            Utility.formatAmount(amount.toFloat())
                        ).apply()
                    }, {
                        dialog.dismiss()
                        onFinish()
                    })
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_enter_valid_amount),
                    Toast.LENGTH_LONG
                ).show();
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_valid_amount),
                Toast.LENGTH_LONG
            ).show();
        }
    }

    fun showLoadingDialog(activity: Activity, title: String) =
        LoyaltyApp.getInstance().runOnUiThread {
            activity.apply {
                val dialogView = LoadingDialogBinding.inflate(LayoutInflater.from(this))
                dialogView.tvContent.text = title;
                val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setView(dialogView.root)
                    .setTitle(this.getString(R.string.loading))
                    .create()

                dialog.setCanceledOnTouchOutside(false) // Prevent dialog from closing on outside touch
                dialog.setCancelable(false) // Prevent dialog from closing on back press

                loadingDialog?.dismiss()
                loadingDialog = dialog
                loadingDialog?.show()
            }
        }

    fun hideLoading(activity: Activity) = activity.runOnUiThread {
        loadingDialog?.cancel()
    }
}

