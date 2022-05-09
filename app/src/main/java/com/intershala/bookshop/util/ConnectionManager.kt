package com.intershala.bookshop.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {

    fun checkConnectivity(context : Context):Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activNetwork : NetworkInfo ?= connectivityManager.activeNetworkInfo

        if (activNetwork ?.isConnected != null){
            return activNetwork.isConnected
        }
        else{
            return false
        }
    }

}