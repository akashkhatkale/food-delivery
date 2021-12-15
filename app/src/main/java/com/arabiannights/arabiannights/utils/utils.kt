package com.arabiannights.arabiannights.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.lang.String.format
import java.text.DateFormat
import java.util.*


fun Context.goToActivity(l : Class<*>){
    Intent(this, l).apply {
        this@goToActivity.startActivity(this)
    }
}


fun showLog(tag : String,e : String) =
    Log.d(tag,e)



