package com.nyp.scaffoldingapp.data.model

import android.media.Image

data class GameCollection(val id : Int, val name : String, val status : CollectionType)

data class GameRef(val id : Int, val name : String, val thumbNailURL : String)

data class Membership(val collectionID : Int, val gameID : Int, val dateAdded : Long)
enum class CollectionType{
    Owned,
    Wishlist,
    Custom
}