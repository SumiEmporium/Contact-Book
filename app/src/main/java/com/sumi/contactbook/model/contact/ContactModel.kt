package com.sumi.contactbook.model.contact

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts")
data class ContactModel(

    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "number") val number: String?,
    @ColumnInfo(name = "address") val address: String?
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}
