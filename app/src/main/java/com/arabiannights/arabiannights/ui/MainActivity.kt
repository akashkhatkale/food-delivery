package com.arabiannights.arabiannights.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.bottomsheet.AddNameBottomSheet
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.ui.main.CartFragment
import com.arabiannights.arabiannights.ui.main.HomeFragment
import com.arabiannights.arabiannights.ui.main.ProfileFragment
import com.arabiannights.arabiannights.ui.main.SearchFragment
import com.arabiannights.arabiannights.ui.orders.CurrentOrderActivity
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.CARTLOG
import com.arabiannights.arabiannights.utils.constants.LOCATIONLOG
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_badge.view.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    var homeFragment =
        HomeFragment()
    var searchFragment =
        SearchFragment()
    var profileFragment =
        ProfileFragment()
    var cartFragment =
        CartFragment()
    var activeFragment : Fragment = homeFragment

    private val repo = FirebaseRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, homeFragment, "home").commit()
        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, searchFragment, "podcast").hide(searchFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, cartFragment, "bookShelf").hide(cartFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.mainFrameLayout, profileFragment, "search").hide(profileFragment).commit()


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                    activeFragment = homeFragment
                    checkOrders()


                    return@setOnNavigationItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(searchFragment).commit()
                    activeFragment = searchFragment
                    checkOrders()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.cartFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(cartFragment).commit()
                    activeFragment = cartFragment
                    cartFragment.refreshItems()
                    Singleton.user.value?.let{user->
                        if(user.currentOrders.size > 0){
                            currentOrderLayout_main.visibility = View.GONE
                        }
                    }

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(profileFragment).commit()
                    activeFragment = profileFragment
                    ///profileFragment.loadOrders()
                    Singleton.user.value?.let{user->
                        if(user.currentOrders.size > 0){
                            currentOrderLayout_main.visibility = View.GONE
                        }
                    }


                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    false
                }
            }
        }



        // badge
        Singleton.cart.observe(this, androidx.lifecycle.Observer {
            if(it.items.isEmpty()){
                bottomNavigationView.removeBadge(R.id.cartFragment)
            }else{
                var amount = 0
                it.items.forEach {
                    amount += it.amount
                }

                bottomNavigationView.getOrCreateBadge(R.id.cartFragment).apply {
                    backgroundColor = resources.getColor(R.color.colorYellow)
                    badgeTextColor = resources.getColor(R.color.colorWhite)
                    number = amount
                    isVisible = true
                }
            }
        })


        // current order layout
        checkOrders()

    }

    private fun checkOrders(){
        Singleton.user.observe(this, androidx.lifecycle.Observer {
            if(it.currentOrders.size > 0){
                currentOrderLayout_main.visibility = View.VISIBLE
                currentOrderLayout_main.setOnClickListener {
                    Intent(this, CurrentOrderActivity::class.java).also{
                        startActivity(it)
                    }
                }
            }else{
                currentOrderLayout_main.visibility = View.GONE
            }
        })
    }


    fun checkName() {
        Singleton.user.value?.let {
            repo.checkName(it){boo->
                if(boo){
                    val b = AddNameBottomSheet(it)
                    b.show(supportFragmentManager,"Add Name")
                }
            }
        }
    }

}