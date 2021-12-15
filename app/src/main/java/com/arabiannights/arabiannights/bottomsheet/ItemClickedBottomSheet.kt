package com.arabiannights.arabiannights.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.modals.AddOns
import com.arabiannights.arabiannights.modals.CartItem
import com.arabiannights.arabiannights.modals.CartModal
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.FOODLOG
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_itemselected.*
import kotlinx.android.synthetic.main.layout_addons.view.*
import kotlinx.android.synthetic.main.row_item.view.*
import kotlin.math.roundToInt

class ItemClickedBottomSheet(val food : FoodModal) : BottomSheetDialogFragment() {

    private var amount = 1
    private var selectedAddOn = ""
    private var selectedAddOnPrice = 0
    private var totalPrice = 0
    private var cartItem = CartItem()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_itemselected, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init
        foodName_foodSelected.text = food.name
        foodDesc_foodSelected.text = food.desc
        foodPrice_foodSelected.text = "₹ ${food.price}"
        totalPrice = food.price.toInt()
        context?.let {c->
            Glide.with(c).load(food.picUrl).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(foodImage_foodSelected)
            foodType_foodSelected.setImageDrawable(if(food.vegOrNot) c.getDrawable(R.drawable.veg_icon) else c.getDrawable(R.drawable.non_veg_icon))
        }
        val ratings = food.totalStars / food.totalRated.toFloat()
        foodRating_foodSelected.text = "${ratings.roundToInt()}"
        cartItem.name = food.name
        cartItem.foodUid = food.uid
        cartItem.picUrl = food.picUrl
        cartItem.vegOrNot = food.vegOrNot
        cartItem.amount = amount
        cartItem.price = food.price.toInt()

        // ad ons
        if(food.addOns.isNotEmpty()){
            // not empty
            addOnsLayout.visibility = View.VISIBLE
            initAddons()
        }else{
            // empty
            addOnsLayout.visibility = View.GONE
        }


        // plus minus
        plusButton_foodSelected.setOnClickListener {
            amount += 1
            amount_foodSelected.text = "${amount}"
            cartItem.amount = amount
            updatePrice()
        }
        minusButton_foodSelected.setOnClickListener {
            if(amount <= 1 ){
                amount = 1
            }else{
                amount -= 1
                amount_foodSelected.text = "${amount}"
            }
            cartItem.amount = amount
            updatePrice()
        }



        // add cart button
        addCart_foodSelected.setOnClickListener {
            Singleton.user.value?.let {u->
                Singleton.cart.value?.let {
                    var c = it

                    if(c.items.isEmpty()){
                        c.items.add(cartItem)
                    }else{
                        for(i in 0 until c.items.size){
                            if(c.items[i].name == cartItem.name && c.items[i].addOns == cartItem.addOns){
                                Log.d(FOODLOG,"Adding item to another item")
                                c.items[i].amount += cartItem.amount
                                break
                            }

                            if(i == c.items.size - 1){
                                Log.d(FOODLOG,"Adding new item")
                                c.items.add(cartItem)
                            }
                        }
                    }

                    Singleton.cart.postValue(c)
                    dismiss()
                }
            } ?: run{
                Singleton.cart.postValue(CartModal(items = arrayListOf(cartItem)))
                dismiss()
            }
        }

    }




    private var addOnsLayouts = arrayListOf<View>()
    private fun initAddons() {
        context?.let {c->
            food.addOns.forEach {f->
                val aLayout = LayoutInflater.from(c).inflate(R.layout.layout_addons,null)
                aLayout.foodOptionRadioButton.text = f.name
                aLayout.foodAddOnPrice.text = "₹ ${f.price}"
                aLayout.foodOptionRadioButton.isChecked = false
                aLayout.foodOptionRadioButton.setOnCheckedChangeListener { compoundButton, b ->
                    if(b){
                        // add add-on
                        val addOn = AddOns(aLayout.foodOptionRadioButton.text.toString(), aLayout.foodAddOnPrice.text.toString().split(" ")[1])
                        cartItem.addOns.add(addOn)
                    }else{
                        // remove add-on
                        val addOn = AddOns(aLayout.foodOptionRadioButton.text.toString(), aLayout.foodAddOnPrice.text.toString().split(" ")[1])
                        cartItem.addOns.remove(addOn)
                    }
                    Log.d(FOODLOG,"Item : ${cartItem}")
                    updatePrice()
                }
                addOnsLayout.addView(aLayout)
                addOnsLayouts.add(aLayout)
            }
        }
    }


    private fun updatePrice(){
        var tempPrice = cartItem.price
        cartItem.addOns.forEach {
            tempPrice += it.price.toInt()
        }
        tempPrice *= amount
        foodPrice_foodSelected.text = "₹ $tempPrice"
    }

}