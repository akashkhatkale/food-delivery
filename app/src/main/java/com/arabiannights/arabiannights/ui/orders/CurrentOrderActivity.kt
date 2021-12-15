package com.arabiannights.arabiannights.ui.orders

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.FIRESTORE_ORDERS
import com.arabiannights.arabiannights.utils.constants.LOCATION_HOME
import com.arabiannights.arabiannights.utils.constants.LOCATION_WORK
import com.arabiannights.arabiannights.utils.constants.ORDERLOG
import com.arabiannights.arabiannights.utils.constants.STATUS_CONFIRMED
import com.arabiannights.arabiannights.utils.constants.STATUS_RECIEVED
import com.arabiannights.arabiannights.utils.showLog
import com.arabiannights.arabiannights.viewholders.CurrentOrderRowViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_current_order.*
import kotlinx.coroutines.*


class CurrentOrderActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_order)
        setSupportActionBar(currentOrderToolbar)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.white_close_icon)
        }

        ordersRecyclerView.adapter = adapter
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)


        // orders
        Singleton.user.value?.let{
            showLog(ORDERLOG, "Orders : ${it}")
            FirebaseFirestore.getInstance().collection(FIRESTORE_ORDERS).whereEqualTo("customerId", it.uid).orderBy("recievedTime", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                value?.let{docs ->
                    showLog(ORDERLOG, "Orders : ${docs.toObjects(OrderModal::class.java)}")
                    val orders = docs.toObjects(OrderModal::class.java)
                    refreshOrders(orders)
                }
            }
        }

    }


    private fun refreshOrders(orders : List<OrderModal>){
        adapter.clear()
        orders.forEach {
            adapter.add(CurrentOrderRowViewHolder(it, this))
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



    private fun hideKeyboard(){
        val v = this.currentFocus
        if(v != null){
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken,0)
        }
    }

}