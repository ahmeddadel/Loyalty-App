package com.zeal.loyaltyapplication.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.zeal.loyaltyapplication.LoyaltyApp
import com.zeal.loyaltyapplication.databinding.ActivityMainBinding
import com.zeal.loyaltyapplication.utils.Constants
import com.zeal.loyaltyapplication.utils.DialogHelper

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val sharedPref by lazy { LoyaltyApp.getSharedPreferencesInstance() }
    private var isDiscountPercentage: Boolean = false
    private var fixedDiscountAmount: Float = 0.0f
    private var percentageDiscountAmount: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        LoyaltyApp.getInstance()
            .runOnBackgroundThreadWithCallback({ // Get discount values from shared preferences
                loadDiscountValues()
            }, {
                initViews()
                setListeners()
            })
    }

    private fun loadDiscountValues() {
        isDiscountPercentage = sharedPref.getBoolean(Constants.IS_DISCOUNT_PERCENTAGE, false)
        fixedDiscountAmount = sharedPref.getFloat(Constants.DISCOUNT_FIXED, 0.0f)
        percentageDiscountAmount = sharedPref.getFloat(Constants.DISCOUNT_PERCENTAGE, 0.0f)
    }

    private fun initViews() {
        binding.swDiscountType.isChecked = isDiscountPercentage // Set discount type switch

        if (isDiscountPercentage) { // Set discount value based on discount type
            binding.tvDiscountAmount.text = "$percentageDiscountAmount %"
        } else {
            binding.tvDiscountAmount.text = "$fixedDiscountAmount USD"
        }
    }

    private fun setListeners() {
        binding.swDiscountType.setOnCheckedChangeListener { _, isChecked ->
            isDiscountPercentage = isChecked
            LoyaltyApp.getInstance()
                .runOnBackgroundThreadWithCallback({ // Save discount type to shared preferences
                    sharedPref.edit().putBoolean(Constants.IS_DISCOUNT_PERCENTAGE, isChecked)
                        .apply()
                }, {
                    initViews()
                })
        }

        binding.btnSetDiscount.setOnClickListener {
            DialogHelper.showDiscountAmountPanDialog(
                this@MainActivity,
                isDiscountPercentage,
                sharedPref
            ) {
                LoyaltyApp.getInstance().runOnBackgroundThreadWithCallback({
                    loadDiscountValues()
                }, {
                    initViews()
                })
            }
        }
    }
}