package com.arabiannights.arabiannights.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.ItemClickedBottomSheet
import com.arabiannights.arabiannights.bottomsheet.SelectLocationBottomSheet
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.modals.FoodModal
import com.arabiannights.arabiannights.ui.MainActivity
import com.arabiannights.arabiannights.utils.OnLocationSelected
import com.arabiannights.arabiannights.utils.constants
import com.arabiannights.arabiannights.utils.constants.APPETIZERS
import com.arabiannights.arabiannights.utils.constants.BURGERS
import com.arabiannights.arabiannights.utils.constants.FRIES
import com.arabiannights.arabiannights.utils.constants.LOCATIONLOG
import com.arabiannights.arabiannights.utils.constants.MOCKTAILS
import com.arabiannights.arabiannights.utils.constants.SHAWARMA
import com.arabiannights.arabiannights.viewholders.ItemRowViewHolder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

class HomeFragment : Fragment(), OnLocationSelected {

    private var categoryButtons = arrayListOf<LinearLayout>()
    private var categoryText = arrayListOf<TextView>()
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val repo = FirebaseRepository()

    private var selectedCategory = SHAWARMA

    lateinit var v : View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // recylcer
        itemsRecyclerView.adapter = adapter


        // permission
        context?.let {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101)
            }else{
                val locationManager = getSystemService(it, LocationManager::class.java)
                val location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                try {
                    val city = hereLocation(it, location?.latitude, location?.longitude)
                    (activity as MainActivity).checkName()
                    setLocationName(city)
                }catch (e : Exception){
                    setLocationName("No location found")
                    Log.d(LOCATIONLOG, "Location not found : ${e.localizedMessage}")
                }

            }
        }





        // on category select
        categoryButtons = arrayListOf(
            shawarmaButton_home, burgersButton_home, friesButton_home, mocktailButton_home, appetizersButton_home
        )
        categoryText = arrayListOf(
            shawarmaText, burgerText, friesText, mocktailText, appetizersText
        )
        shawarmaButton_home.setOnClickListener {
            if(selectedCategory != SHAWARMA)
                onCategorySelect(shawarmaButton_home, SHAWARMA)
        }
        burgersButton_home.setOnClickListener {
            if(selectedCategory != BURGERS)
                onCategorySelect(burgersButton_home, BURGERS)
        }
        friesButton_home.setOnClickListener {
            if(selectedCategory != FRIES)
                onCategorySelect(friesButton_home, FRIES)
        }
        mocktailButton_home.setOnClickListener {
            if(selectedCategory != MOCKTAILS)
                onCategorySelect(mocktailButton_home, MOCKTAILS)
        }
        appetizersButton_home.setOnClickListener {
            if(selectedCategory != APPETIZERS)
                onCategorySelect(appetizersButton_home, APPETIZERS)
        }
        onCategorySelect(shawarmaButton_home, SHAWARMA)


        // on location clikc
        locationHeadingLayout.setOnClickListener {
            SelectLocationBottomSheet(this, "").show(childFragmentManager, "location")
        }
    }

    private fun onCategorySelect(selected : LinearLayout, category : String) {
        adapter.clear()
        selectedCategory = category
        loadItems(category)
        categoryButtons.forEachIndexed { index, it ->
            if(it != selected){
                // unselected
                it.setBackgroundResource(R.drawable.edittext_background)
                categoryText.get(index).setTextColor(resources.getColor(R.color.colorPrimary))
            }else{
                // selected
                it.setBackgroundResource(R.drawable.button_background)
                categoryText.get(index).setTextColor(resources.getColor(R.color.colorWhite))
            }
        }
    }

    private fun refreshItems(i : List<FoodModal>){
        adapter.clear()
        if(i.isEmpty()){
            setView(View.INVISIBLE, View.VISIBLE)
        }else{
            setView(View.INVISIBLE, View.INVISIBLE)
            i.forEach {
                context?.let {c->
                    adapter.add(ItemRowViewHolder(requireFragmentManager(),c, it))
                }
            }
        }
    }

    private fun loadItems(category: String) {
        setView(View.VISIBLE, View.INVISIBLE)
        GlobalScope.launch(Dispatchers.IO) {
            async {
                val items = repo.loadFoodItems(category)
                withContext(Dispatchers.Main){
                    refreshItems(items)
                }
            }
        }
    }


    private fun hereLocation(c : Context, latitude: Double?, longitude: Double?): String {
        var cityName = ""
        if(latitude != null && longitude != null){
            val geoCoder = Geocoder(c, Locale.getDefault())
            var address = listOf<Address>()
            try{
                address = geoCoder.getFromLocation(latitude, longitude,10)
                if(address.isNotEmpty()){
                    for (it in address) {
                        if(it.locality != null && it.locality.isNotEmpty()){
                            cityName = it.locality
                            break
                        }
                    }
                }
            }catch (e : Exception){
                Log.d(LOCATIONLOG, "Location eexception : ${e.localizedMessage}")
            }
        }

        return cityName
    }

    fun setLocationName(name : String){
        v.locationText_home.text = name
    }

    private fun setView(sVisibility : Int, tVisibility : Int){
        shimmerHomeContainer.visibility = sVisibility
        status_home.visibility = tVisibility
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        context?.let {
            if(requestCode == 101){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val locationManager = getSystemService(it, LocationManager::class.java)
                    val location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    try {
                        val city = hereLocation(it,location?.latitude, location?.longitude)
                        setLocationName(city)
                    }catch (e : Exception){
                        setLocationName("No location found")
                        Log.d(LOCATIONLOG, "Location not found : ${e.localizedMessage}")
                    }
                }else{
                    Toasty.warning(it,"Location permission not given").show()

                }
                (activity as MainActivity).checkName()
            }
        }

    }

    override fun onSelected(add: AddressModal) {
        locationText_home.text = add.tag
    }
}