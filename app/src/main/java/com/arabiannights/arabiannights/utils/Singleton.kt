package com.arabiannights.arabiannights.utils

import androidx.lifecycle.MutableLiveData
import com.arabiannights.arabiannights.modals.CartModal
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.modals.UserModal

object Singleton {
    var user : MutableLiveData<UserModal> = MutableLiveData()
    var cart : MutableLiveData<CartModal> = MutableLiveData()

    var order : MutableLiveData<OrderModal> = MutableLiveData()

    var currentOrders : ArrayList<OrderModal> = arrayListOf()
}
