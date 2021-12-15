package com.arabiannights.arabiannights.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.ui.location.NewLocationActivity
import com.arabiannights.arabiannights.utils.OnLocationSelected
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.viewholders.LocationRowViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.bottomsheet_select_location.*

class SelectLocationBottomSheet(val listener : OnLocationSelected, var uid : String) : BottomSheetDialogFragment() {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private var isSelected = ""
    private var selectedLoc = AddressModal()
    private var allAddresses = listOf<AddressModal>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_select_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSelected = uid
        // recycler
        allLocationsRecyclerView.adapter = adapter
        Singleton.user.value?.let {
            allAddresses = it.address
            refreshLocations(it.address)
        }
        adapter.setOnItemClickListener { item, view ->
            val v = item as LocationRowViewHolder
            isSelected = v.add.uid
            selectedLoc = v.add
            refreshLocations(allAddresses)
        }

        //
        newLocationLayout_row.setOnClickListener {
            Intent(requireContext(),NewLocationActivity::class.java).apply {
                this.putExtra("location",AddressModal())
                startActivity(this)
            }
        }

        //
        submitButton_selectLocation.setOnClickListener {
            listener.onSelected(selectedLoc)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        Singleton.user.value?.let {
            refreshLocations(it.address)
        }
    }

    private fun refreshLocations(address: List<AddressModal>) {
        adapter.clear()
        context?.let {c->
            address.forEach {
                adapter.add(LocationRowViewHolder(c,it,isSelected == it.uid))
            }
        }

    }
}