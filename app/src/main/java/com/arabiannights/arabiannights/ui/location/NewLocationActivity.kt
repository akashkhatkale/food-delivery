package com.arabiannights.arabiannights.ui.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.modals.AddressModal
import com.arabiannights.arabiannights.utils.Singleton
import com.arabiannights.arabiannights.utils.constants.LOCATION_HOME
import com.arabiannights.arabiannights.utils.constants.LOCATION_OTHER
import com.arabiannights.arabiannights.utils.constants.LOCATION_WORK
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_location.*
import kotlinx.coroutines.*
import java.util.*

class NewLocationActivity : AppCompatActivity() {

    lateinit var location : AddressModal
    private val repo = FirebaseRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)
        setSupportActionBar(newLocationToolbar)
        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.black_back_icon)
            it.setDisplayHomeAsUpEnabled(true)
        }

        location = intent.getParcelableExtra("location") ?: AddressModal()
        newLocation.setOnClickListener {
            hideKeyboard()
        }

        // tag click
        refreshTag()
        homeTag_newLocation.setOnClickListener {
            location.tag = LOCATION_HOME
            refreshTag()
        }
        workTag_newLocation.setOnClickListener {
            location.tag = LOCATION_WORK
            refreshTag()
        }
        otherTag_newLocation.setOnClickListener {
            location.tag = LOCATION_OTHER
            refreshTag()
        }



        // location name
        locationNameEditText.setOnClickListener {
            locationNameEditText.isEnabled = false
            Intent(this, AddMapLocationActivity::class.java).also {
                it.putExtra("lon", location.longitude)
                it.putExtra("lat", location.latitude)
                startActivityForResult(it, 101)
            }
        }



        // save
        saveButton_newLocation.setOnClickListener {
            hideKeyboard()
            Singleton.user.value?.let {u->
                val locationName = locationNameEditText.text.toString()
                val completeAdd = completeAddressEditText.text.toString()
                val landmark = landmarkNameEditText.text.toString()
                val uid = UUID.randomUUID().toString()
                if(locationName.isNotEmpty() && completeAdd.isNotEmpty()){
                    location.uid = uid
                    location.locationName = locationName
                    location.completeAddress = completeAdd
                    location.landmark = landmark
                    GlobalScope.launch(Dispatchers.IO) {
                        async {
                            if(repo.saveLocation(u.uid, location)){
                                withContext(Dispatchers.Main){
                                    finish()
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    Toasty.error(this@NewLocationActivity,"Error in adding location").show()
                                }
                            }
                        }
                    }
                }else{
                    Toasty.warning(this,"Enter full details").show()
                }
            }

        }
    }

    private fun refreshTag() {
        homeTag_newLocation.background = if(location.tag == LOCATION_HOME) getDrawable(R.drawable.button_background) else getDrawable(R.drawable.edittext_background)
        workTag_newLocation.background = if(location.tag == LOCATION_WORK) getDrawable(R.drawable.button_background) else getDrawable(R.drawable.edittext_background)
        otherTag_newLocation.background = if(location.tag == LOCATION_OTHER) getDrawable(R.drawable.button_background) else getDrawable(R.drawable.edittext_background)

        homeTag_newLocation.setTextColor(resources.getColor(if(location.tag == LOCATION_HOME) R.color.colorWhite else R.color.colorGray))
        workTag_newLocation.setTextColor(resources.getColor(if(location.tag == LOCATION_WORK) R.color.colorWhite else R.color.colorGray))
        otherTag_newLocation.setTextColor(resources.getColor(if(location.tag == LOCATION_OTHER) R.color.colorWhite else R.color.colorGray))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 101 && data != null ){
                val lon = data.getDoubleExtra("lon",0.0)
                val lat = data.getDoubleExtra("lat",0.0)
                val locationName = data.getStringExtra("name") ?: ""
                location.locationName = locationName
                location.longitude = lon
                location.latitude = lat
                locationNameEditText.setText(locationName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationNameEditText.isEnabled = true
    }

    private fun hideKeyboard(){
        val v = this.currentFocus
        if( v != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken,0)
        }
    }
}