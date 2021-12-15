package com.arabiannights.arabiannights.modals

data class CartItem(
    var name : String = "",
    var foodUid : String = "",
    var amount : Int = 0,
    var price : Int = 0,
    var picUrl : String = "",
    var vegOrNot : Boolean = false,
    var addOns: ArrayList<AddOns> = arrayListOf()
)