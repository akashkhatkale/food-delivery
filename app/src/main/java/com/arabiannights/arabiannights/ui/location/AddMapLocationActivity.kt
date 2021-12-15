package com.arabiannights.arabiannights.ui.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.utils.constants.LOCATIONLOG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_map_location.*
import java.io.IOException
import java.util.jar.Manifest

class AddMapLocationActivity : AppCompatActivity(){


    private lateinit var mMap: GoogleMap
//
    var latitude : Double = 0.0
    var longitude : Double = 0.0
    var locationName = ""
//
//    private lateinit var locationManager : LocationManager
//    private lateinit var bestProvider : String
//    private lateinit var criteria: Criteria


    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_map_location)

        // assign var
        supportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        // get fused location
        client = LocationServices.getFusedLocationProviderClient(this)


        // check permission
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(LOCATIONLOG, "Permission granted")
            getCurrentLocation()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 44)
        }



        backButton_map.setOnClickListener {
            finish()
        }

        confirmLocation_map.setOnClickListener {
            if(latitude != 0.0 && longitude != 0.0 && locationName != ""){
                val intent = Intent()
                intent.putExtra("lon", longitude)
                intent.putExtra("lat", latitude)
                intent.putExtra("name", locationName)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

    }


    private fun getCurrentLocation(){
        var task = client.lastLocation
        task.addOnSuccessListener {
            if(it != null){
                supportMapFragment.getMapAsync(object : OnMapReadyCallback{
                    override fun onMapReady(p0: GoogleMap) {
                        mMap = p0
                        mMap.setOnMapClickListener {l->
                            mMap.clear()
                            latitude = l.latitude
                            longitude = l.longitude
                            val options = MarkerOptions().position(l)
                            p0.animateCamera(CameraUpdateFactory.newLatLngZoom(l,17.0f))
                            p0.addMarker(options)
                            locationName_map.text = getLocationName(l)
                        }
                        val latLng = LatLng(it.latitude, it.longitude)
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                        val options = MarkerOptions().position(latLng)
                        p0.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f))
                        p0.addMarker(options)
                        locationName_map.text = getLocationName(latLng)
                    }
                })
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 44){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                Toasty.error(this,"Location permission denied").show()
            }
        }
    }

    private fun getLocationName(latLng : LatLng) : String {
        val geocoder = Geocoder(this)
        var locationList: MutableList<Address> = mutableListOf<Address>()
        try {
            locationList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if(locationList.size > 0) {
            val address = locationList.get(0)
            locationName = address.getAddressLine(0)
        }

        return locationName
    }




//    override fun onLocationChanged(location: Location) {
//        val mLastLocation = location
//        val postion = LatLng(mLastLocation!!.latitude,mLastLocation!!.longitude)
//
//        val cameraPosition = CameraPosition.Builder().target(postion).zoom(17.0.toFloat()).build()
//        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
//        mMap.moveCamera(cameraUpdate)
//    }
//
//
//
//    private fun checkLocationPermission(permission : String, requestCode : Int){
//        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED) {
//            // PERMISSION NOT GRANTED
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.d("LOCLOG", "Permission denied")
//                ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
//            }
//        }else {
//            // PERMISSION GRANTED
//            Log.d("LOCLOG","Permission already granted")
//
//            locationManager =  getSystemService(LOCATION_SERVICE) as LocationManager
//            criteria = Criteria()
//            bestProvider = locationManager.getBestProvider(criteria, true) ?: ""
//            var location = locationManager.getLastKnownLocation(bestProvider)
//            Log.d("LOCLOG","LOC : ${location}")
//            if (location != null) {
//                onLocationChanged(location);
//            }
//            locationManager.requestLocationUpdates(bestProvider, 20000, 0.0f, this)
//
//            mMap.isMyLocationEnabled = true
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == 101){
//            if(grantResults.size  > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                // LOCATION PERMISSION GRANTED
//                Toast.makeText(this,"Permission granted", Toast.LENGTH_SHORT).show()
//                checkLocationPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 101)
//            }else{
//                // LOCATION PERMISSION DENIES
//                Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onMapReady(p0: GoogleMap?) {
//        mMap = p0!!
//
//        checkLocationPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 101 )
//
//        if(latitude != 0.0 && longitude != 0.0){
//            val location = LatLng(latitude, longitude)
//            mMap.addMarker(MarkerOptions().position(location))
//            val cameraPosition =
//                CameraPosition.Builder().target(location).zoom(17.0.toFloat()).build()
//            val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
//            mMap.moveCamera(cameraUpdate)
//        }else{
//
//        }
//
//
//        // MAPS
//        mMap.setOnMapClickListener {
//            mMap.clear()
//            latitude = it.latitude
//            longitude = it.longitude
//            val geocoder = Geocoder(this)
//            var locationList: MutableList<Address> = mutableListOf<Address>()
//            try {
//                locationList = geocoder.getFromLocation(latitude,longitude,1)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            if(locationList.size > 0) {
//                val address = locationList.get(0)
//                locationName = address.getAddressLine(0)
//            }
//            locationName_map.text = locationName
//            mMap.addMarker(MarkerOptions().position(LatLng(latitude,longitude)))
//        }
//    }
}