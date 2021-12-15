package com.arabiannights.arabiannights.viewholders

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.ItemClickedBottomSheet
import com.arabiannights.arabiannights.modals.AddOns
import com.arabiannights.arabiannights.modals.CartItem
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.ui.main.CartFragment
import com.arabiannights.arabiannights.utils.Singleton
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.row_cart_item.view.*
import kotlinx.android.synthetic.main.row_item.view.*
import kotlinx.android.synthetic.main.row_item.view.foodImage_foodRow
import kotlin.math.roundToInt

class CartItemRowViewHolder( val c : Context, var item : CartItem, var index : Int, var f : CartFragment) : Item() {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val i = viewHolder.itemView
        i.foodName_cartItem.text = item.name

        var price = item.price
        if(item.addOns.isNotEmpty()){
            item.addOns.forEach {
                price += it.price.toInt()
            }
        }
        i.foodPrice_cartItem.text = "₹ ${price * item.amount}"
        i.amount_cartItem.text = item.amount.toString()
        Glide.with(c).load(item.picUrl).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(i.foodImage_cartItem)
        i.foodType_cartItem.setImageDrawable(if(item.vegOrNot) c.getDrawable(R.drawable.veg_icon) else c.getDrawable(R.drawable.non_veg_icon))

        i.plusButton_cartItem.setOnClickListener {
            Singleton.cart.value?.let {
                var c = it
                c.items[index].amount += 1
                Singleton.cart.postValue(c)
                f.refreshItems()
            }
        }

        i.minusButton_cartItem.setOnClickListener {
            Singleton.cart.value?.let {
                if(item.amount > 1){
                    var c = it
                    c.items[index].amount -= 1
                    Singleton.cart.postValue(c)
                    f.refreshItems()
                }else{
                    var c = it
                    c.items.remove(item)
                    Singleton.cart.postValue(c)
                    f.refreshItems()
                }
            }
        }


        // recycler view
        if(item.addOns.isNotEmpty()){
            // there are add-ons
            i.foodDesc_cartItem.text = "Check add-ons"
            i.foodDesc_cartItem.background = c.getDrawable(R.drawable.button_background)
            i.foodDesc_cartItem.setTextColor(c.resources.getColor(R.color.colorWhite))
        }else{
            // no add-ons
            i.foodDesc_cartItem.text = "No add-ons"
            i.foodDesc_cartItem.background = c.getDrawable(R.drawable.edittext_background)
            i.foodDesc_cartItem.setTextColor(c.resources.getColor(R.color.colorGray))
        }
    }


    override fun getLayout(): Int {
        return R.layout.row_cart_item
    }
}









