package com.arabiannights.arabiannights.viewholders

import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.modals.AddOns
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.row_addons.view.*

class AddOnItemRowViewHolder(val a : AddOns) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_addOnRow.text = a.name
    }

    override fun getLayout(): Int {
        return R.layout.row_addons
    }
}