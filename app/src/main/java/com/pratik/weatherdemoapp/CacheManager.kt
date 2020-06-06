package com.pratik.weatherdemoapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class CacheManager private constructor() {

    fun putString(key: String, value: String) {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)

        return sharedPreferences.getString(key, null)
    }

    fun putBoolean(key: String, value: Boolean) {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean(key, false)
    }

    fun putLong(key: String, value: Long) {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String): Long {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)

        return sharedPreferences.getLong(key, 0)
    }


    fun putInt(key: String, value: Int) {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)

        return sharedPreferences.getInt(key, 0)
    }

    fun putJSONObjectForJSONArray(key: String, jsonObject: JSONObject) {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var jsonArray = JSONArray()

        if (sharedPreferences.getString(key, null) == null) {
            jsonArray.put(jsonObject)
        } else {
            jsonArray = JSONArray(sharedPreferences.getString(key, null))
            jsonArray.put(jsonObject)
        }
        editor.putString(key, jsonArray.toString()).apply()
    }

//    fun removeJSONObjectForJSONArray(index : Int) {
//        val sharedPreferences =
//            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        var jsonArray = JSONArray(sharedPreferences.getString(key, null))
//        jsonArray.remove(jsonArray.)
//
//        editor.putString(key, jsonArray.toString()).apply()
//    }

    fun getJSONArray(key: String): JSONArray {
        val sharedPreferences =
            sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE)
        val string = sharedPreferences.getString(key, null)
        val jsonArray = JSONArray(string)
        return jsonArray
    }

    fun clear() {
        sContext!!.getSharedPreferences(cacheContainer, Context.MODE_PRIVATE).edit().clear().apply()
    }

    companion object {

        private val TAG = CacheManager::class.java.simpleName

        const val cacheContainer = "container"
        private var sInstance: CacheManager? = null
        private var sContext: Context? = null

        fun getInstance(context: Context): CacheManager {
            if (sInstance == null) {
                sInstance = CacheManager()
            }
            sContext = context

            return sInstance!!
        }
    }
}