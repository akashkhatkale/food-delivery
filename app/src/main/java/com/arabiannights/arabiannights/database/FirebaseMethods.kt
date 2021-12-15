package com.arabiannights.arabiannights.database


import android.util.Log
import com.arabiannights.arabiannights.modals.*
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.FIRESTORE_ITEMS
import com.arabiannights.arabiannights.utils.constants.FIRESTORE_ORDERS
import com.arabiannights.arabiannights.utils.constants.FIRESTORE_USER
import com.arabiannights.arabiannights.utils.constants.FOODLOG
import com.arabiannights.arabiannights.utils.constants.HOMELOG
import com.arabiannights.arabiannights.utils.constants.LOCATIONLOG
import com.arabiannights.arabiannights.utils.constants.LOGINLOG
import com.arabiannights.arabiannights.utils.constants.ORDERLOG
import com.arabiannights.arabiannights.utils.constants.SEARCHLOG
import com.arabiannights.arabiannights.utils.constants.STATUS_CANCELLED
import com.arabiannights.arabiannights.utils.constants.STATUS_RECIEVED
import com.arabiannights.arabiannights.utils.showLog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.type.DateTime
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseMethods {

    private val firestore = FirebaseFirestore.getInstance()


    // user functions
    suspend fun getCurrentUser(user : FirebaseUser?)  =
        try{
            checkUserInDatabase("", user)
        }catch (e : Exception){
            Log.d(LOGINLOG,"Error in getting user : ${e.localizedMessage}")
        }
    suspend fun checkUserInDatabase(name : String, u : FirebaseUser?) : Boolean{
        return try {
            val user = UserModal(u?.uid ?: "",name,u?.email ?: "",u?.phoneNumber ?: "",u?.photoUrl?.toString()?: "",joinedDate = System.currentTimeMillis().toString())
            val isExists = firestore.collection(FIRESTORE_USER).document(user.uid).get().await()
            if(isExists.exists()){
                Log.d(LOGINLOG,"User existsss")

                Singleton.user.postValue(isExists.toObject(UserModal::class.java))
                true
            }else{
                Log.d(LOGINLOG,"User doesnt existsss")
                val token = FirebaseInstanceId.getInstance().instanceId.await().token
                user.fcmToken = token
                firestore.collection(FIRESTORE_USER).document(user.uid).set(user).await()
                Singleton.user.postValue(user)
                Log.d(LOGINLOG,"User Createdddd")
                true
            }
        }catch (e : java.lang.Exception){
            Log.d(LOGINLOG,"Error in checking db : ${e.localizedMessage}")
            false
        }
    }
    fun checkName(user : UserModal, action : (Boolean) -> Unit) {
        action(user.name.isEmpty())
    }
    suspend fun saveName(uid : String, name: String) : Boolean = try {
        val ref = firestore.collection(FIRESTORE_USER).document(uid)
            .update("name",name).await()
        true
    }catch (e : Exception){
        showLog(HOMELOG, "Error in saving name : ${e.localizedMessage}")
        false
    }
    suspend fun saveLocation(uid : String, location : AddressModal) : Boolean = try{
        val ref = firestore.collection(FIRESTORE_USER).document(uid).update("address",FieldValue.arrayUnion(location)).await()
        val u = Singleton.user.value!!
        u.address.add(location)
        Singleton.user.postValue(u)
        true
    }catch (e : Exception){
        showLog(LOCATIONLOG, "Error in saving location : ${e.localizedMessage}")
        false
    }










    // load items
    suspend fun loadFoodItems(category: String) : List<FoodModal> =
        try {
            val ref = firestore.collection(FIRESTORE_ITEMS).whereEqualTo("category", category).get().await()
            ref.toObjects(FoodModal::class.java)
        }catch (e : Exception){
            showLog(FOODLOG, "Error in loading food : ${e.localizedMessage}")
            emptyList()
        }


    // search
    suspend fun searchItem(query : String) : List<FoodModal> =
        try{
            firestore.collection(FIRESTORE_ITEMS).whereArrayContains("tags",query).get().await().toObjects(FoodModal::class.java)
        }catch (e : Exception){
            showLog(SEARCHLOG, "Error in searching food : ${e.localizedMessage}")
            emptyList()
        }



    // orders
    suspend fun placeOrder(uid : String, order : OrderModal) : String =
        try{
            val ref = firestore.collection(FIRESTORE_ORDERS).document()
            var u = Singleton.user.value!!
            if(!u.currentOrders.contains(order.orderId) && u.currentOrders.size == 0){
                u.currentOrders.add(order.orderId)
            }else{
                // todoo : if another order is placed
                "Cannot place another order"
            }
            order.orderId = ref.id
            order.status = STATUS_RECIEVED
            order.recievedTime = System.currentTimeMillis()
            ref.set(order).await()
            Singleton.cart.postValue(CartModal())
            Singleton.order.postValue(OrderModal())
            Singleton.currentOrders.add(order)
            firestore.collection(FIRESTORE_USER).document(uid).update("currentOrders",FieldValue.arrayUnion(order.orderId)).await()

            "Success"
        }catch (e : Exception){
            showLog(ORDERLOG,"Error in placing orders: ${e.localizedMessage}")
            "Error in placing order"
        }

    suspend fun fetchCurrentOrder() : OrderModal =
            try{
                if(Singleton.currentOrders.size > 0 ){
                    Singleton.currentOrders[0]
                }else{
                    Singleton.user.value?.let {
                        if(it.currentOrders.size > 0){
                            var order = firestore.collection(FIRESTORE_ORDERS).whereEqualTo("orderId", it.currentOrders[0]).get().await().toObjects(OrderModal::class.java)[0]
                            Singleton.currentOrders.add(order)
                            order
                        }else{
                            var order = firestore.collection(FIRESTORE_ORDERS).whereEqualTo("customerId", it.uid).get().await().toObjects(OrderModal::class.java)[0]
                            Singleton.currentOrders.add(order)
                            order
                        }
                    }?: kotlin.run {
                        OrderModal()
                    }
                }

            }catch (e : Exception){
                showLog(ORDERLOG,"Error in fetching current order : ${e.localizedMessage}")
                OrderModal()
            }

    suspend fun updateRequest(request : String, orderId :String) : Boolean =
        try{
            firestore.collection(FIRESTORE_ORDERS).document(orderId).update("request",request).await()
            true
        }catch (e : java.lang.Exception){
            showLog(ORDERLOG,"Error in updating rest req : ${e.localizedMessage}")
            false
        }

    suspend fun cancelOrder(orderId: String, uid : String) : Boolean =
        try{
            firestore.collection(FIRESTORE_ORDERS).document(orderId).update("status", STATUS_CANCELLED).await()
            firestore.collection(FIRESTORE_USER).document(uid).update("currentOrders", FieldValue.arrayRemove(orderId)).await()
            var user = Singleton.user.value!!
            user.currentOrders.remove(orderId)
            Singleton.user.postValue(user)
            true
        }catch (e : java.lang.Exception){
            showLog(ORDERLOG,"Error in cancelling order : ${e.localizedMessage}")
            false
        }

}