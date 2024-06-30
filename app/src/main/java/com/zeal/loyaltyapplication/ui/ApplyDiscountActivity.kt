package com.zeal.loyaltyapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.zeal.loyaltyapplication.LoyaltyApp
import com.zeal.loyaltyapplication.R
import com.zeal.loyaltyapplication.databinding.ActivityApplyDiscountBinding
import com.zeal.loyaltyapplication.utils.Constants
import com.zeal.loyaltyapplication.utils.DialogHelper
import com.zeal.loyaltyapplication.utils.Utility


class ApplyDiscountActivity : AppCompatActivity() {

    private val binding by lazy { ActivityApplyDiscountBinding.inflate(layoutInflater) }
    private val sharedPref by lazy { LoyaltyApp.getSharedPreferencesInstance() }
    private var isDiscountPercentage: Boolean = false
    private var fixedDiscountAmount: Float = 0.0f
    private var percentageDiscountAmount: Float = 0.0f
    private var transactionAmount: Float = 0.0f
    private var cardId: String = ""
    private var discountAmount: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        if (intent.action == Constants.ACTION_APPLY_DISCOUNT) {
            val extras = intent.extras
            if (extras != null) {
                transactionAmount = extras.getFloat(Constants.TRANSACTION_AMOUNT, 0.0f)
                cardId = extras.getString(Constants.CARD_ID, "")
            } else {
                setResult(RESULT_CANCELED)
                finish()
            }
        } else {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        DialogHelper.showLoadingDialog(
            this@ApplyDiscountActivity,
            getString(R.string.finding_discount)
        )
        LoyaltyApp.getInstance().runOnUiThreadWithDelay({
            LoyaltyApp.getInstance().runOnBackgroundThreadWithCallback({
                loadDiscountValues()
            }, {
                applyDiscount()
                sendDiscountResult() // Send back the discount amount to Payment Application
            })
        }, 2000) // Simulate a delay of 2 seconds
    }

    private fun loadDiscountValues() {
        isDiscountPercentage = sharedPref.getBoolean(Constants.IS_DISCOUNT_PERCENTAGE, false)
        fixedDiscountAmount = sharedPref.getFloat(Constants.DISCOUNT_FIXED, 0.0f)
        percentageDiscountAmount = sharedPref.getFloat(Constants.DISCOUNT_PERCENTAGE, 0.0f)
    }

    private fun applyDiscount() {
        discountAmount = if (isDiscountPercentage) {
            transactionAmount * (percentageDiscountAmount / 100)
        } else {
            fixedDiscountAmount
        }

        discountAmount = Utility.formatAmount(discountAmount)

        if (discountAmount > 0) {
            var dialogMsg = getString(R.string.applying_discount_for) + "\n**** **** **** "
            dialogMsg += if (cardId.length > 3) {
                cardId.substring(cardId.length - 4)
            } else {
                cardId
            }

            DialogHelper.showLoadingDialog(
                this@ApplyDiscountActivity,
                dialogMsg
            )
        }

        transactionAmount -= discountAmount
        if (transactionAmount < 0) {
            transactionAmount = 0.0f
        }
    }

    private fun sendDiscountResult() {
        LoyaltyApp.getInstance().runOnUiThreadWithDelay({
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.DISCOUNT_AMOUNT, discountAmount)
            resultIntent.putExtra(Constants.TRANSACTION_AMOUNT, transactionAmount)
            setResult(RESULT_OK, resultIntent)
            finish() // Finish the activity and return to Payment Application
        }, 1000) // Simulate a delay of 1 second
    }

    override fun finish() {
        DialogHelper.hideLoading(this@ApplyDiscountActivity)
        super.finish()
    }
}