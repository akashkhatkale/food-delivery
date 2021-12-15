package com.arabiannights.arabiannights.viewholders

import android.content.Context
import android.view.View
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.utils.constants.LOCATION_HOME
import com.arabiannights.arabiannights.utils.constants.LOCATION_WORK
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.row_location.view.*

class LocationRowViewHolder(val c : Context,val add : AddressModal, val isSelected : Boolean) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val i = viewHolder.itemView
        i.locationIcon_row.setImageDrawable(c.getDrawable(if(add.tag == LOCATION_HOME) R.drawable.white_home_icon else if(add.tag == LOCATION_WORK) R.drawable.white_work_icon else R.drawable.white_location_icon))
        i.locationName_row.text = add.locationName
        i.locationTag_row.text = add.tag
        i.selectIcon_row.visibility = if(isSelected) View.VISIBLE else View.INVISIBLE
        i.newLocationLayout.background = c.getDrawable(if(isSelected) R.drawable.edittext_background2 else R.drawable.greyborderbutton_background)
    }

    override fun getLayout(): Int {
        return R.layout.row_location
    }
}