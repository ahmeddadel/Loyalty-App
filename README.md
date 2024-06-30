# Loyalty Application

## Overview

This Loyalty Application is designed to work in conjunction with a Payment Application to manage and apply discounts on incoming transactions. The application is part of the Zeal Android Payment Flow Assignment, where it interfaces with the Payment Application to exchange and manage data related to payment transactions, applying potential discounts, and adjusting the final charge amount accordingly.

## Features

- **Discount Management**: Set specific discounts on incoming transactions.
- **Transaction Processing**: Apply discounts and instruct the Payment Application on how to proceed based on the remaining amount.
- **Integration with Payment Application**: Seamless communication with the Payment Application to manage transaction details.

## User Flow

1. The Payment Application sends transaction details to the Loyalty Application.
2. The Loyalty Application applies any applicable discounts.
3. Based on the discount, the Loyalty Application instructs the Payment Application to:
   - Conclude the transaction if the discount covers the entire amount.
   - Proceed with the bank transaction if there is a remaining amount to be charged.
   - Complete the transaction normally if no discount is applied.

## Discount Management

- **Set Discounts**: Users can set a specific discount amount for incoming transactions. If the discount amount is set to 0, no discount is applied.
- **Apply Discounts**: Discounts are applied directly to the transaction amount before proceeding with the payment process.

## Transaction Processing

- **Full Discount**: If the discount covers the entire transaction amount, the process is concluded without contacting the bank.
- **Partial Discount**: If there is a remaining amount to be charged after applying the discount, the transaction continues with the bank for the updated amount.
- **No Discount**: If no discount is set, the transaction proceeds normally.
