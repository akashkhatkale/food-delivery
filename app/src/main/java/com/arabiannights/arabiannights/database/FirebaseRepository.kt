package com.arabiannights.arabiannights.database

import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.modals.UserModal
import com.google.firebase.auth.FirebaseUser

class FirebaseRepository {

    private val methods = FirebaseMethods()

    // user methods
    suspend fun getCurrentUser(user : FirebaseUser?)  = methods.getCurrentUser(user)
    fun checkName(user : UserModal, action : (Boolean) -> Unit) = methods.checkName(user){
        action(it)
    }
    suspend fun saveName(uid : String, name: String) : Boolean = methods.saveName(uid, name)
    suspend fun saveLocation(uid : String, location : AddressModal) : Boolean = methods.saveLocation(uid, location)


    // items
    suspend fun loadFoodItems(category: String) : List<FoodModal> = methods.loadFoodItems(category)


    // search
    suspend fun searchItem(query : String) : List<FoodModal> = methods.searchItem(query)


    // orders
    suspend fun placeOrder(uid : String, order : OrderModal) : String = methods.placeOrder(uid , order)
    suspend fun updateRequest(request : String, orderId :String) : Boolean = methods.updateRequest(request, orderId)
    suspend fun cancelOrder(orderId :String, uid : String) : Boolean = methods.cancelOrder(orderId, uid)
}