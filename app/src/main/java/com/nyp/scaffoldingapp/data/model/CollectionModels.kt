package com.nyp.scaffoldingapp.data.model


data class GameCollection(val id : Int, val name : String, val type : CollectionType)

data class GameRef(val id : Int, val name : String, val thumbNailURL : String)

data class Membership(val collectionID : Int, val gameID : Int, val dateAdded : Long)

data class CollectionData( val collection : List<GameCollection> = emptyList(), val games : List<GameRef> = emptyList(), val membership : List<Membership> = emptyList(), val playSessions : List<PlaySession> = emptyList())
data class PlaySession(
    val id : Int,
    val gameId : Int,
    val datePlayed : Long,
    val durationMinutes : Int,
    val players : List<String>,
    val winner : String?
)
enum class CollectionType{
    Owned,
    Wishlist,
    Custom
}

enum class PlayError{
    GAME_NOT_FOUND,
    NO_PLAYERS,
    BLANK_NAME,
    DUPLICATE_NAME,
    WINNER_NOT_A_PLAYER,
    DURATION_TOO_SHORT,
    DURATION_TOO_LONG,
    DATE_IN_FUTURE
}