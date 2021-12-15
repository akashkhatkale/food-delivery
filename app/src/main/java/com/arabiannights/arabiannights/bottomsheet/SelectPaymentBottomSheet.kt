package com.arabiannights.arabiannights.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.ui.orders.CurrentOrderActivity
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.LOCATION_HOME
import com.arabiannights.arabiannights.utils.constants.LOCATION_WORK
import com.arabiannights.arabiannights.utils.constants.PAYMENT_CASH
import com.arabiannights.arabiannights.utils.constants.PAYMENT_ONLINE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.bottomsheet_select_payment.*
import kotlinx.coroutines.*

class SelectPaymentBottomSheet() : BottomSheetDialogFragment() {

    private lateinit var order : OrderModal
    private val repo = FirebaseRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_select_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Singleton.order.value?.let {
            order = it
        } ?: run{
            dismiss()
        }

        // init
        totalAmount_selectPayment.text = "Total amount to be paid : ₹ ${order.totalAmount}"
        icon_selectPayment.setBackgroundResource(if(order.deliveryLocation.tag == LOCATION_HOME) R.drawable.white_home_icon else if(order.deliveryLocation.tag == LOCATION_WORK) R.drawable.white_work_icon else R.drawable.white_location_icon)
        address_selectPayment.text = order.deliveryLocation.locationName


        // button
        cashPayment_selectPayment.setOnClickListener {
            order.paymentMode = PAYMENT_CASH
            buttonSelected()
        }
        onlinePayment_selectPayment.setOnClickListener {
            order.paymentMode = PAYMENT_ONLINE
            buttonSelected()
        }


        placeOrder_selectPayment.setOnClickListener {
            // place order
            if(order.paymentMode == ""){
                context?.let { c->
                    Toasty.warning(c,"Select a payment mode").show()
                }
                return@setOnClickListener
            }
            setView(0.5f, false, View.VISIBLE)
            GlobalScope.launch(Dispatchers.IO) {
                async {
                    Singleton.user.value?.let {
                        var r = repo.placeOrder(it.uid, order)
                        if(r == "Success"){
                            // success
                            // go to order screen
                            withContext(Dispatchers.Main){
                                context?.let{c->
                                    Toasty.success(c,"Order placed successfully").show()
                                    dismiss()
                                    Intent(c, CurrentOrderActivity::class.java).also {
                                        it.putExtra("order",order)
                                        startActivity(it)
                                    }
                                    setView(1.0f, true, View.INVISIBLE)
                                }
                            }
                        }else{
                            // failure
                            withContext(Dispatchers.Main){
                                context?.let{c->
                                    Toasty.error(c,r).show()
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    private fun setView(bAlpha : Float, bEnabled: Boolean, pVisibility : Int){
        placeOrder_selectPayment.alpha = bAlpha
        placeOrder_selectPayment.isEnabled = bEnabled
        placeOrderProgressBar.visibility = pVisibility
    }


    private fun buttonSelected(){
        val r = if(order.paymentMode == PAYMENT_CASH) R.drawable.edittext_background2 else R.drawable.greyborderbutton_background
        val r2 = if(order.paymentMode == PAYMENT_ONLINE) R.drawable.edittext_background2 else R.drawable.greyborderbutton_background
        cashPayment_selectPayment.setBackgroundResource(r)
        onlinePayment_selectPayment.setBackgroundResource(r2)
        selectCashIcon_selectPayment.visibility = if(order.paymentMode == PAYMENT_CASH) View.VISIBLE else View.INVISIBLE
        selectOnlineIcon_selectPayment.visibility = if(order.paymentMode == PAYMENT_ONLINE) View.VISIBLE else View.INVISIBLE
    }
}