package com.sumi.contactbook.activity

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sumi.contactbook.BaseApplication
import com.sumi.contactbook.R
import com.sumi.contactbook.databinding.ActivityAddUserBinding
import com.sumi.contactbook.model.contact.ContactModel
import com.sumi.contactbook.roomdb.RoomRepository
import com.sumi.contactbook.viewmodel.ContactViewModel
import java.io.IOException
import java.util.*

class AddUserActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var binding: ActivityAddUserBinding
    private lateinit var viewModel: ContactViewModel
    private lateinit var repository: RoomRepository
    private var mMap: GoogleMap? = null
    private var mMarker: Marker? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var startMarker: Marker

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_user)

        initUi()
    }

    private fun initUi() {

        binding.toolBar.title.setText(getString(R.string.txt_add))
        binding.toolBar.ivSearch.visibility = View.GONE

        repository = BaseApplication.getApplicationInstance().repository

        viewModel = ViewModelProvider(
            this@AddUserActivity,
            ContactViewModel.FACTORY(repository)
        ).get(ContactViewModel::class.java)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val address = binding.tvAddress.text.toString()

            val contact = ContactModel(name, phoneNumber, address)
            viewModel.insert(contact)
            finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.flMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)

    }

    fun requestPermissionForLocation() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted()!!) {
                        getCurrentLocation()
                    }else{
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!isLocationPermissionGiven()) {
            requestPermissionForLocation()
        }else{
            getCurrentLocation()
        }

        mMap?.setOnMarkerDragListener(this)

    }


    override fun onMarkerDrag(p0: Marker) {
        setStartLocation(p0.position.latitude, p0.position.longitude, "")
    }

    override fun onMarkerDragEnd(p0: Marker) {
        Log.e("markermap","MarkerDragEnd")

        setStartLocation(p0.position.latitude, p0.position.longitude, "")
    }

    override fun onMarkerDragStart(p0: Marker) {
        Log.e("markermap","onMarkerDragStart")
    }

    fun isLocationPermissionGiven(): Boolean {

        return !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    }

    private fun getCurrentLocation() {
      val  locationRequest = LocationRequest.create().apply {
            interval = (10 * 1000).toLong()
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates?.isLocationPresent == true){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }


    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result
                    setStartLocation(mLastLocation!!.latitude, mLastLocation.longitude, "")
                } else {
                    Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setStartLocation(lat: Double, lng: Double, addr: String){
        var address = "Current address"
        if (addr.isEmpty()){
            val gcd = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>
            try {
                addresses = gcd.getFromLocation(lat, lng, 1)
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            address = addr
        }
        val icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.ic_marker))

        mMap?.clear()
        startMarker = mMap?.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title("Address")
                .icon(icon)
                .draggable(true)
        )!!
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(17f)
            .build()
        mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        Log.e("kjkj","kk${address}")

        binding.tvAddress.setText(address)
       // fromLocationTxt.text = String.format("From: Near %s", address)
    }
}