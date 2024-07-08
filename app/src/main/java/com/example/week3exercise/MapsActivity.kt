package com.example.week3exercise

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.week3exercise.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
     companion object Constants {
        val INTENT_KEY_ADDRESS = "address"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var confirm: Button
    private var currentAddress: Address? = null
    private lateinit var currentLocation: ImageButton
    private lateinit var locationProvider: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLocation = findViewById(R.id.imageButton)
        currentLocation.setOnClickListener {
            //Toast.makeText(this, "ok pressed", Toast.LENGTH_LONG).show()=
            checkPermissions()

        }

        confirm = findViewById(R.id.confirmButton)
        confirm.setOnClickListener {
            if (currentAddress != null) {
                val tweetIntent = Intent(this, TweetActivity::class.java)
                tweetIntent.putExtra(Constants.INTENT_KEY_ADDRESS, currentAddress)
                startActivity(tweetIntent)

            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




     fun checkPermissions() {
        //Toast.makeText(this, "ok pressed", Toast.LENGTH_LONG).show()
        //val permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "access to gps", Toast.LENGTH_LONG).show()
            //Log.d("MapsActivity", "GPS permission granted")

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            //Toast.makeText(this, "ok pressed", Toast.LENGTH_LONG).show()
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MapsActivity", "GPS permission granted")
            } else {
                //Log.d("MapsActivity", "GPS permission denied")

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    Toast.makeText(
                        this,
                        "allow location",
                        Toast.LENGTH_LONG
                    ).show()

                    val settingIntent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivityForResult(settingIntent, 100)


                    settingIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {

        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener { latLng: LatLng ->
            Log.d("MapsActivity", "Long press at ${latLng.latitude}, ${latLng.longitude}")
            mMap.clear()

            val geocoder = Geocoder(this)
            val results: List<Address> = try {
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    10
                ) as List<Address>
            } catch (exception: Exception) {
                exception.printStackTrace()
                Log.e("MainActivity", "fail to retrieve results: $exception")
                listOf<Address>()
            }

            if (results.isNotEmpty()) {
                val firstResult = results.first()
                val streetAddress = firstResult.getAddressLine(0)
                currentAddress = firstResult

                val marker = MarkerOptions().position(latLng).title(streetAddress)
                mMap.addMarker(marker)
                updateConfirmButtom(firstResult)
            }

        }
    }




    private fun updateConfirmButtom(address: Address) {
        confirm.text = address.getAddressLine(0)

        val greenColor = ContextCompat.getColor(this,R.color.colorPrimary)
        val checkImage = ContextCompat.getDrawable(this,R.color.colorPrimaryDark)

        confirm.setBackgroundColor(greenColor)
        confirm.setCompoundDrawablesRelativeWithIntrinsicBounds(checkImage,null,null, null)

    }

}


