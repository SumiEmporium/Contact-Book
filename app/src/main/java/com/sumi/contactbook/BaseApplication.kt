package com.sumi.contactbook

import android.app.Application
import com.sumi.contactbook.prefs.AppPreference
import com.sumi.contactbook.roomdb.ContactRoomDatabase
import com.sumi.contactbook.roomdb.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BaseApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ContactRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RoomRepository(database.contactDao()) }

    companion object{
        lateinit var application: BaseApplication

        fun getApplicationInstance() = application
    }

    override fun onCreate() {
        super.onCreate()

        application = this
        AppPreference.init(this)
    }
}