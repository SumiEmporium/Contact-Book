package com.sumi.contactbook.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sumi.contactbook.model.login.LoginResponse
import java.lang.reflect.Type

object AppPreference {

    private const val PREF_FILE_NAME = "ContactPreference"
    private const val PREF_USER = "user"
    private lateinit var mGSonInstance: Gson
    private lateinit var preferences: SharedPreferences
    private const val MODE = Context.MODE_PRIVATE


    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE)

        mGSonInstance = Gson()
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    var cachedUser: LoginResponse.ResponseObj?
        set(value) {
            val userDataStr = mGSonInstance.toJson(value)
            preferences.edit { it.putString(PREF_USER, userDataStr) }
        }
        get() {
            val userDataStr = preferences.getString(PREF_USER, "")
            val type: Type = genericType<LoginResponse.ResponseObj>()
            return mGSonInstance.fromJson(userDataStr, type)
        }
}