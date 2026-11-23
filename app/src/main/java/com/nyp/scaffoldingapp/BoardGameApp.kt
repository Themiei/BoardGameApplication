package com.nyp.scaffoldingapp

import android.app.Application
import com.nyp.scaffoldingapp.data.CollectionRepository

class BoardGameApp : Application() {
    val collectionRepository = CollectionRepository()
}