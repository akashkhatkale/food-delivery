package com.arabiannights.arabiannights.modals

data class CartModal(
    var uid : String = "",
    var price : Int = 0,
    var address : AddressModal = AddressModal(),
    var userUid : String = "",
    var items : ArrayList<CartItem> = arrayListOf()
)