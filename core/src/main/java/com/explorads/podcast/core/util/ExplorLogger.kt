package com.explorads.podcast.core.util

import android.util.Log
import com.explorads.podcast.core.BuildConfig

object ExplorLogger {


    fun log(message:String?){
        if (BuildConfig.DEBUG){
            Log.d("EXPLOR_LOG", message?:"")
        }
    }

}