package com.sumi.contactbook.roomdb

import androidx.annotation.WorkerThread
import com.sumi.contactbook.model.contact.ContactModel
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val contactDao: ContactDao) {

    val allDataContact: Flow<MutableList<ContactModel>> = contactDao.getAllContacts()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dataModel: ContactModel) {
        contactDao.insert(dataModel)
    }
}