package com.arabiannights.arabiannights.modals


data class UserModal(
    val uid : String = "",
    var name : String = "",
    val email : String = "",
    val number : String = "",
    val profileUrl : String = "",
    var fcmToken : String = "",
    var joinedDate : String = "",
    var address : ArrayList<AddressModal> = arrayListOf(),
    var currentOrders : ArrayList<String> = arrayListOf()
) {
    fun convertStringToLong() : Long{
        return joinedDate.toLong()
    }
}