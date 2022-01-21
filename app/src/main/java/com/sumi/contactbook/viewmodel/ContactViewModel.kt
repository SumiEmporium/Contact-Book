package com.sumi.contactbook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sumi.contactbook.model.contact.ContactModel
import com.sumi.contactbook.roomdb.RoomRepository
import com.sumi.contactbook.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: RoomRepository) : ViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::ContactViewModel)
    }

    val allContacts: LiveData<MutableList<ContactModel>> = repository.allDataContact.asLiveData()
    fun insert(dataModel: ContactModel) {
        viewModelScope.launch {
            repository.insert(dataModel)
        }
    }
}