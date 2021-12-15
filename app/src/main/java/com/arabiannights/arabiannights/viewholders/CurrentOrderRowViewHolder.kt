package com.arabiannights.arabiannights.viewholders

import android.content.Context
import android.view.View
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.utils.constants
import com.arabiannights.arabiannights.utils.constants.ORDERLOG
import com.arabiannights.arabiannights.utils.constants.STATUS_CANCELLED
import com.arabiannights.arabiannights.utils.constants.STATUS_CONFIRMED
import com.arabiannights.arabiannights.utils.constants.STATUS_DELIVERED
import com.arabiannights.arabiannights.utils.constants.STATUS_DELIVERY
import com.arabiannights.arabiannights.utils.constants.STATUS_RECIEVED
import com.arabiannights.arabiannights.utils.showLog
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_current_order.view.*
import kotlinx.coroutines.*

class CurrentOrderRowViewHolder(val order : OrderModal, val context : Context) : Item() {
    private var repo = FirebaseRepository()

    private var statusColor = hashMapOf(
        STATUS_RECIEVED to R.color.recieved,
        STATUS_CONFIRMED to R.color.confirmed,
        STATUS_DELIVERY to R.color.delivery,
        STATUS_DELIVERED to R.color.delivered,
        STATUS_CANCELLED to R.color.cancelled
    )

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.totalAmount_currentOrder.text = "Total amount to be paid : ₹${order.totalAmount}"
        viewHolder.itemView.icon_currentOrder.background = context.getDrawable(if(order.deliveryLocation.tag == constants.LOCATION_HOME) R.drawable.white_home_icon else if(order.deliveryLocation.tag == constants.LOCATION_WORK) R.drawable.white_work_icon else R.drawable.white_location_icon)
        viewHolder.itemView.address_currentOrder.text = order.deliveryLocation.locationName
        viewHolder.itemView.requestText_currentOrder.setText(order.request)
        showLog(ORDERLOG, "${order}")
        viewHolder.itemView.orderStatus_currentOrders.text = "Order ${order.status}"
        viewHolder.itemView.orderStatus_currentOrders.backgroundTintList = context.getColorStateList(statusColor[order.status] ?: R.color.recieved)

        // food items
        var food = ""
        order.foodItems.forEachIndexed { index, it ->
            food += "${it.name} x ${it.amount}"

            if (index != order.foodItems.size - 1){
                food += ", "
            }
        }
        viewHolder.itemView.foodItems_currentOrder.text = food


        // send request button
        viewHolder.itemView.sendRequestButton.setOnClickListener {
            val req = viewHolder.itemView.requestText_currentOrder.text.toString()
            if(req.isNotEmpty()){
                viewHolder.itemView.sendRequestButton.isEnabled = false
                hideKeyboard(context, viewHolder.itemView)
                GlobalScope.launch(Dispatchers.IO) {
                    async {
                        if(repo.updateRequest(req, order.orderId)){
                            withContext(Dispatchers.Main){
                                Toasty.success(context, "Your request was sent").show()
                                hideKeyboard(context, viewHolder.itemView)
                            }
                        }
                        withContext(Dispatchers.Main){
                            viewHolder.itemView.sendRequestButton.isEnabled = true
                            hideKeyboard(context, viewHolder.itemView)
                        }
                    }
                }
            }
        }


        // cancel button
        if(order.status == STATUS_RECIEVED){
            viewHolder.itemView.cancelOrderButton.alpha = 1.0f
            viewHolder.itemView.cancelOrderButtonLayout.visibility = View.VISIBLE
        }else{
            if(order.status == STATUS_CANCELLED || order.status == STATUS_DELIVERED){
                viewHolder.itemView.cancelOrderButtonLayout.visibility = View.GONE
            }else{
                viewHolder.itemView.cancelOrderButtonLayout.visibility = View.VISIBLE
                viewHolder.itemView.cancelOrderButton.alpha = 0.5f
            }
        }
        viewHolder.itemView.cancelOrderButton.setOnClickListener {
            if(order.status == "recieved"){
                GlobalScope.launch(Dispatchers.IO) {
                    async {
                        repo.cancelOrder(order.orderId, order.customerId)
                    }
                }
            }else{
                if (order.status == STATUS_CANCELLED){
                    Toasty.warning(context, "Order cancelled already").show()

                }else{
                    Toasty.warning(context, "Cannot cancel order now").show()
                }
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.row_current_order
    }
}