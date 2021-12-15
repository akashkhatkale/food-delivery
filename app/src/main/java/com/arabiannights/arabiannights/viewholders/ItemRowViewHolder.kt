package com.arabiannights.arabiannights.viewholders

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.ItemClickedBottomSheet
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.utils.Singleton
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.row_item.view.*
import kotlin.math.roundToInt

class ItemRowViewHolder(val fm : FragmentManager,val c : Context, val food : FoodModal) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val i = viewHolder.itemView
        i.foodName_foodRow.text = food.name
        i.foodDesc_foodRow.text = food.desc
        i.foodPrice_foodRow.text = "₹ ${food.price}"
        Glide.with(c).load(food.picUrl).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(i.foodImage_foodRow)
        i.foodType_foodRow.setImageDrawable(if(food.vegOrNot) c.getDrawable(R.drawable.veg_icon) else c.getDrawable(R.drawable.non_veg_icon))

        val ratings = food.totalStars / food.totalRated.toFloat()
        i.foodRating_foodRow.text = "${ratings.roundToInt()}"

        i.addCart_foodRow.setOnClickListener {
            Singleton.user.value?.let{
                val b = ItemClickedBottomSheet(food)
                b.show(fm,"ItemClicked")
            }

        }
    }

    override fun getLayout(): Int {
        return R.layout.row_item
    }
}