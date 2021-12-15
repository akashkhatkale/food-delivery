package com.arabiannights.arabiannights.modals

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class OrderModal(
        var orderId : String = "",
        var customerId : String = "",
        var paymentMode : String = "",
        var deliveryLocation : AddressModal = AddressModal(),
        var totalAmount : String = "",
        var foodItems : @RawValue ArrayList<CartItem> = arrayListOf(),
        var recievedTime : Long = 0,
        var dispatchedTime : Long = 0,
        var deliveredTime : Long = 0,
        var status : String = "",
        var request : String = "",
        var transactionId : String = ""
) : Parcelable

