package com.arabiannights.arabiannights.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.AddNameBottomSheet
import com.arabiannights.arabiannights.bottomsheet.SelectLocationBottomSheet
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.modals.OrderModal
import com.arabiannights.arabiannights.ui.orders.CurrentOrderActivity
import com.arabiannights.arabiannights.ui.start.LoginActivity
import com.arabiannights.arabiannights.utils.OnLocationSelected
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants
import com.arabiannights.arabiannights.utils.showLog
import com.arabiannights.arabiannights.viewholders.CurrentOrderRowViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_profile.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ProfileFragment : Fragment(), OnLocationSelected {

    private var adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    lateinit var event : ListenerRegistration


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // init
        Singleton.user.value?.let{user->
            view.name_profile.text = if(user.name.isNotEmpty()) user.name else "No Name"
            view.mobile_profile.text = user.number
            view.editButton_profile.setOnClickListener {
                val b = AddNameBottomSheet(user)
                b.show(childFragmentManager,"Add Name")
            }

            event = FirebaseFirestore.getInstance().collection(constants.FIRESTORE_ORDERS).whereEqualTo("customerId", user.uid).orderBy("recievedTime", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                value?.let{docs ->
                    val orders = docs.toObjects(OrderModal::class.java)
                    refreshOrders(orders)
                }
            }
        }

        view.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Intent(requireContext(), LoginActivity::class.java).apply {
                startActivity(this)
                requireActivity().finish()
            }
        }

        view.ordersButton.setOnClickListener {
            Intent(requireContext(), CurrentOrderActivity::class.java).also{
                startActivity(it)
            }
        }

        view.locationButton.setOnClickListener {
            SelectLocationBottomSheet(this, "profile").show(childFragmentManager,"profile")
        }



    }

    private fun refreshOrders(orders : List<OrderModal>){
        adapter.clear()
        orders.forEach {
            adapter.add(CurrentOrderRowViewHolder(it, requireContext()))
        }
    }

    override fun onSelected(add: AddressModal) {

    }

//    fun loadOrders(){
//        if(!this::event.isInitialized){
//            Singleton.user.value?.let{user->
//                event = FirebaseFirestore.getInstance().collection(constants.FIRESTORE_ORDERS).whereEqualTo("customerId", user.uid).orderBy("recievedTime", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
//                    value?.let{docs ->
//                        val orders = docs.toObjects(OrderModal::class.java)
//                        refreshOrders(orders)
//                    }
//                }
//            }
//
//        }
//    }


}
