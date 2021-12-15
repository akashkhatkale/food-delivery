package com.arabiannights.arabiannights.modals

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodModal(
    val uid : String = "",
    val name : String = "",
    val desc : String = "",
    val price : String = "",
    var category : String = "",
    var vegOrNot : Boolean = false,
    var availableOrNot : Boolean = true,
    var picUrl : String = "",
    var totalRated : Int = 0,
    var totalStars : Int = 0,
    var addOns : List<AddOns> = listOf(),
    var tags : List<String> = listOf()
) : Parcelable

@Parcelize
data class AddOns(
    val name : String = "",
    val price : String = ""
) : Parcelable