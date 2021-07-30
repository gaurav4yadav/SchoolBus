
package com.slef.schoolbustracker

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.slef.schoolbustracker.models.user
import com.squareup.okhttp.internal.DiskLruCache


@IgnoreExtraProperties

data class LocationLogging(
    var Latitude: Double? = 0.0,
    var Longitude: Double? = 0.0
)

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var currentUser: User? = null
        val TAG = "MapsActivity"
    }
   var sydney = LatLng(23.2295537,77.392109)
    private lateinit var auth: FirebaseAuth

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    //var mark = MarkerOptions().position(sydney)
    //map.addMarker(MarkerOptionss)

  // var marker: Marker =map?.addMarker(MarkerOptions())

var marker:Marker? = null


    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )


    }


    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 20000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation



//                    val uid = FirebaseAuth.getInstance().currentUser?.uid
//                    val rootRef =  FirebaseFirestore.getInstance()
//                    val usersRef = rootRef.collection("users")
//                    val uidRef = uid?.let { usersRef.document(it) }
//                    if (uidRef != null) {
//                        uidRef.get()
//                                .addOnSuccessListener { document ->
//                                    if (document != null) {
//                                        val latitude = document.getDouble("latitude")
//                                        val longitude = document.getDouble("longitude")
//                                        Log.d(TAG, ", " + location.latitude + location.longitude)
//                                    } else {
//                                        Log.d(TAG, "No such document")
//                                    }
//                                }
//                                .addOnFailureListener { exception ->
//                                    Log.d(TAG, "get failed with ", exception)
//                                }
//                    }
                    lateinit var databaseRef: DatabaseReference
                    databaseRef = FirebaseDatabase.getInstance().getReference("driver")

                    val currentUser = auth.currentUser
                    val locationlogging = LocationLogging(location.latitude, location.longitude)

                    sydney = LatLng(location.getLatitude(), location.getLongitude())
                    if(marker!=null)
                  marker!!.remove()
                    marker=map.addMarker(MarkerOptions().position(sydney).title("here"))
                    map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                    map.moveCamera(CameraUpdateFactory.zoomTo(16f))
//                    var markerOptions = MarkerOptions()
//                    markerOptions.position(sydney)
//                    markerOptions.title("Current Position")
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                   //markerOptions = map.addMarker(markerOptions)

//                    perth =map.addMarker(
//                        MarkerOptions()
//                            .position(sydney)
//                            .draggable(true)
//                            .title("located")
//                    )
                   // map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                   // map.animateCamera(CameraUpdateFactory.zoomTo(11F));




//                    val myRef = database.getReference("message")
//
//                    myRef.setValue("Hello, World!")
                    var trya:String=""
                    var database: FirebaseFirestore ?=null
                    database = FirebaseFirestore.getInstance()
                    var myemail:String?=null
                    val user = Firebase.auth.currentUser
                    user?.let {

                        myemail = user.email.toString()
                    }


                //    Toast.makeText(baseContext,"$myemail", Toast.LENGTH_LONG).show()
                    database.collection("driver").document(myemail.toString().trim()).get()
                        .addOnSuccessListener { document->
                            if(document!=null)
                            {
                                trya=document.getString("uniquedb").toString()


                                val driver = dc(myemail.toString(),location.latitude.toDouble(),location.longitude.toDouble())
                                databaseRef.child( trya.toString()).setValue(driver)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Locations shared",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Error occured while writing your location to the database",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }



                            }
                        }




                   // databaseRef.child("user").child(currentUser.toString()).setValue("A999999")


                }

            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        auth = Firebase.auth

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Toast.makeText(
            applicationContext,
            "Press middle button to run in background ",
            Toast.LENGTH_LONG
        ).show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        getLocationAccess()
    }



}



