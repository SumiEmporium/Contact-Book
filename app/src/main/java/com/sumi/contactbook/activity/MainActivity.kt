package com.sumi.contactbook.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sumi.contactbook.BaseApplication
import com.sumi.contactbook.R
import com.sumi.contactbook.adapter.ContactAdapter
import com.sumi.contactbook.databinding.ActivityMainBinding
import com.sumi.contactbook.roomdb.RoomRepository
import com.sumi.contactbook.viewmodel.ContactViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ContactViewModel
    private lateinit var repository: RoomRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initUi()
    }

    private fun initUi() {
        repository = BaseApplication.getApplicationInstance().repository

        viewModel = ViewModelProvider(
            this,
            ContactViewModel.FACTORY(repository)
        ).get(ContactViewModel::class.java)

        viewModel.allContacts.observe(this, {
            Log.e("kkkk", "" + it)
            binding.rvContact.adapter = ContactAdapter(it)
        })

        binding.clAdd.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
    }
}