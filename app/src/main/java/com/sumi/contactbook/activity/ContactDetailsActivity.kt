package com.sumi.contactbook.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sumi.contactbook.R
import com.sumi.contactbook.databinding.ActivityContactDetailsBinding
import com.sumi.contactbook.model.contact.ContactModel
import com.sumi.contactbook.util.TAG_CONTACT
import java.io.IOException
import java.util.*


class ContactDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityContactDetailsBinding
    private var mMap: GoogleMap? = null
    private lateinit var contact: ContactModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_details)

        initUi()
    }

    private fun initUi() {
        intent?.let {
            contact = intent.extras?.getParcelable(TAG_CONTACT)!!
            binding.contact = contact
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.flMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        goToLocationFromAddress(contact.address)

        if (!isLocationPermissionGiven()) {
            requestPermissionForLocation()
        } else {

        }
    }

    fun isLocationPermissionGiven(): Boolean {

        return !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
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
                        goToLocationFromAddress(contact.address)
                    } else {
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


    fun goToLocationFromAddress(strAddress: String?) {

        val coder = Geocoder(this, Locale.getDefault())
        val address: List<Address>?
        try {

            address = coder.getFromLocationName(strAddress, 5)
            if (address != null) {

                try {
                    val location: Address = address[0]
                    val latLng = LatLng(location.getLatitude(), location.getLongitude())
                    val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                    mMap?.addMarker(markerOptions)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(17f))
                } catch (er: IndexOutOfBoundsException) {
                    Toast.makeText(this, "Location isn't available", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Address", "Not found")
        }
    }
}