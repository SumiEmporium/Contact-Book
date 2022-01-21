package com.sumi.contactbook.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumi.contactbook.model.contact.ContactModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: ContactModel)

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<MutableList<ContactModel>>

}