package com.arabiannights.arabiannights.ui.main

import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.SelectLocationBottomSheet
import com.arabiannights.arabiannights.bottomsheet.SelectPaymentBottomSheet
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.modals.CartModal
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.utils.OnLocationSelected
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants
import com.arabiannights.arabiannights.utils.constants.CARTLOG
import com.arabiannights.arabiannights.viewholders.CartItemRowViewHolder
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() , OnLocationSelected {

    private var adapter = GroupAdapter<GroupieViewHolder>()

    private var selectedAddress : AddressModal = AddressModal()

    lateinit var remoteConfig : FirebaseRemoteConfig

    private val defaults = mapOf<String, String>(
        "deliveryFee" to "0"
    )

    private var orderModal = OrderModal()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaults)


        return inflater.inflate(R.layout.fragment_cart, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // defauls
        deliveryPrice_cart.text = "₹ " + remoteConfig.getString("deliveryFee")


        // recycler
        cartRecyclerView.adapter = adapter
        refreshItems()


        // bottom sheet
        locationLayout.setOnClickListener {
            val b = SelectLocationBottomSheet(this, selectedAddress.uid)
            b.show(requireFragmentManager(), "Select Location")
        }


        // submit
        orderButton_cart.setOnClickListener {
            Singleton.cart.value?.let{c->
                if(c.items.size > 0 ){
                    if(selectedAddress != AddressModal()){
                        Singleton.user.value?.let {
                            orderModal.customerId = it.uid
                            orderModal.totalAmount = totalPrice.toString()
                            orderModal.foodItems = c.items
                            orderModal.deliveryLocation = selectedAddress
                            Singleton.order.postValue(orderModal)
                            SelectPaymentBottomSheet().also {p->
                                p.show(requireFragmentManager(), "Payment")
                            }
                        }
                    }else{
                        context?.let {con->
                            Toasty.warning(con,"Please select delivery location").show()
                        }
                    }

                }
            }
        }


        // listener
        activity?.let{
            Singleton.order.observe(it, Observer {
                if(it == OrderModal()){
                    adapter.clear()
                    Singleton.cart.postValue(CartModal())
                    totalPrice = 0
                    selectedAddress  = AddressModal()
                    locationIcon_cart.setImageDrawable(resources.getDrawable(R.drawable.white_location_icon))
                    locationTag_cart.text = "Select a location"
                    locationName_cart.text = "No location selected"
                    priceLayout_cart.visibility = View.GONE
                    locationLayout.visibility = View.GONE
                    setView(View.INVISIBLE, View.VISIBLE)
                }
            })
        }



    }

    fun refreshItems() {
        adapter.clear()
        totalPrice = 0
        setView(View.VISIBLE, View.INVISIBLE)
        Singleton.cart.value?.let {c->
            if(c.items.isNotEmpty()){
                setView(View.INVISIBLE, View.INVISIBLE)
                priceLayout_cart.visibility = View.VISIBLE
                locationLayout.visibility = View.VISIBLE
                refreshPrice(c)
                c.items.forEachIndexed { index, i->
                    adapter.add(CartItemRowViewHolder(context!!, i,index, this))
                }
            }else{
                Singleton.cart.postValue(CartModal())
                Singleton.order.postValue(OrderModal())
                totalPrice = 0
                selectedAddress  = AddressModal()
                priceLayout_cart.visibility = View.GONE
                locationLayout.visibility = View.GONE
                setView(View.INVISIBLE, View.VISIBLE)
            }
        } ?: run{
            priceLayout_cart.visibility = View.GONE
            locationLayout.visibility = View.GONE
            setView(View.INVISIBLE, View.VISIBLE)
        }
    }

    var totalPrice = 0
    private fun refreshPrice(c : CartModal){
        c.items.forEach {
            if(it.addOns.isEmpty()){
                totalPrice += it.amount * it.price
                Log.d(CARTLOG,"Price : ${totalPrice}, amount : ${it.amount * it.price}")
            }else{
                var addOns = 0
                it.addOns.forEach {a->
                    addOns += a.price.toInt()
                }
                totalPrice += it.amount * (it.price + addOns)
            }
            itemPrice_cart.text = "₹ ${totalPrice}"
            itemTotal_cart.text = "₹ ${totalPrice + remoteConfig.getString("deliveryFee").toInt()}"
        }
    }


    private fun setView(sVisibility : Int , tVisibility : Int){
        shimmerCartContainer.visibility = sVisibility
        status_cart.visibility = tVisibility
    }

    override fun onSelected(add: AddressModal) {
        if(add != AddressModal()){
            selectedAddress = add
            context?.let { c->
                locationIcon_cart.setImageDrawable(c.getDrawable(if(add.tag == constants.LOCATION_HOME) R.drawable.white_home_icon else if(add.tag == constants.LOCATION_WORK) R.drawable.white_work_icon else R.drawable.white_location_icon))
                locationName_cart.text = add.locationName
                locationTag_cart.text = add.tag
            }
        }
    }


}