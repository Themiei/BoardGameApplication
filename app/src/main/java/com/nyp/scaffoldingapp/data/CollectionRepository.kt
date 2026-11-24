package com.nyp.scaffoldingapp.data

import android.R
import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.GameRef
import com.nyp.scaffoldingapp.data.model.Membership
import com.nyp.scaffoldingapp.data.model.PlaySession
import com.nyp.scaffoldingapp.data.model.CollectionType
import com.nyp.scaffoldingapp.data.model.PlayError

import java.util.Calendar
class CollectionRepository {

    private val collectionList : MutableList<GameCollection> = mutableListOf<GameCollection>()
    private val gamesList : MutableList<GameRef> = mutableListOf<GameRef>()
    private val membershipList : MutableList<Membership> = mutableListOf<Membership>()
    private val playSessionList : MutableList<PlaySession> = mutableListOf<PlaySession>()
    private var collectionIDCounter : Int = 0
    private var playSessionIDCounter : Int = 0
    private val MAX_PLAY_MINUTES = 1440

    public fun ReadCollections(): MutableList<GameCollection> {
        return collectionList
    }

    public fun ReadGames() : List<GameRef> {
        return gamesList
    }

    public fun ReadMemberships() : List<Membership> {
        return membershipList
    }

    init {
        val wishlist = GameCollection(nextCollectionID(), "WishList", CollectionType.Wishlist)
        val owned = GameCollection(nextCollectionID(), "Owned", CollectionType.Owned)
        collectionList.add(wishlist)
        collectionList.add(owned)
    }

    public fun CreateCollection(name : String) : Boolean {
        val trim = name.trim()
        if(isNameTaken(trim) || trim.isBlank())
            return false
        val customCollection = GameCollection(nextCollectionID(), trim, CollectionType.Custom)
        collectionList.add(customCollection)
        return true

    }

    public fun DeleteCollection(id : Int) {
        if(!isBuiltIn(id))
        {
            collectionList.removeAll{it.id == id}
            membershipList.removeAll{it.collectionID == id}
        }
        return
    }

    public fun AddGameToCollection(game : GameRef, collectionID : Int) {
        storeGameIfNew(game)
        if(isMember(collectionID, game.id))
            return
        val owned = collectionOfType(CollectionType.Owned)
        val wishlist = collectionOfType(CollectionType.Wishlist)
        if(owned?.id == collectionID)
            membershipList.removeAll{it.collectionID == wishlist?.id && it.gameID == game.id}
        val newMembership = Membership(collectionID, game.id, getTodayStartMillis())
        membershipList.add(newMembership)
    }

    public fun RemoveGameFromCollection(gameEntry : Int, collectionEntry : Int){
        val foundMembership = isMember(collectionEntry, gameEntry)
        if(foundMembership)
            membershipList.removeAll{it.collectionID == collectionEntry && it.gameID == gameEntry}
    }

    fun isMember(collectionID : Int, gameID : Int) : Boolean
    {

        val membershipMatch = membershipList.any{it.collectionID == collectionID && it.gameID == gameID}
        return membershipMatch
    }

    fun collectionOfType(type : CollectionType) : GameCollection?
    {
        val collection = collectionList.find{it.status == type}
        return collection
    }

    fun isBuiltIn(collectionID : Int) : Boolean
    {
        val collection = collectionList.find{it.id == collectionID}
        if(collection != null)
        {
            if(collection.status == CollectionType.Wishlist || collection.status == CollectionType.Owned)
                return true
            return false
        }
        return false
    }

    public fun isNameTaken(name : String, id : Int?= null) : Boolean
    {
        val result = name.filterNot{it.isWhitespace()}.lowercase()
        val list = collectionList.filterNot{it.id == id}
        val bool = list.any{it.name.filterNot{it.isWhitespace()}.lowercase() == result}
        return bool
    }

    fun nextCollectionID() : Int
    {
        val counter = collectionIDCounter
        collectionIDCounter += 1
        return counter
    }

    fun storeGameIfNew(game : GameRef)
    {
        if(gamesList.any{it.id == game.id})
            return
        gamesList.add(game)
        return
    }

    public fun gamesIn(collectionID : Int) : List<GameRef>
    {
        val membershipPings = membershipList.filter{it.collectionID == collectionID}.map{it.gameID}.toSet()
       return gamesList.filter{it.id in membershipPings}
    }

    public fun collectionsContaining(gameID : Int) : List<GameCollection>
    {
        val listOfCollectionIDs = membershipList.filter{it.gameID == gameID}.map{it.collectionID}.toSet()
        return collectionList.filter{it.id in listOfCollectionIDs}
    }

    public fun findCollection(id : Int) : GameCollection? {
        return collectionList.find{it.id == id}
    }

    public fun gameCountInCollection(id : Int) : Int {
        return gamesIn(id).count()
    }

    public fun renameCollection(id: Int, name : String) : Boolean {
        val trim = name.trim()
        if(isNameTaken(trim, id) || trim.isBlank() || isBuiltIn(id))
            return false
        val index = collectionList.indexOfFirst{it.id == id}
        if(index != -1)
        {
            collectionList[index] = collectionList[index].copy(name = trim)
            return true
        }
        return false
    }

    private fun nextPlaySessionID() : Int
    {
        val counter = playSessionIDCounter
        playSessionIDCounter += 1
        return counter
    }

    fun validatePlayers(players : List<String>) : PlayError?
    {
        val cleanPlayers = cleanNames(players)
        if(cleanPlayers.isEmpty())
            return PlayError.NO_PLAYERS
        if(cleanPlayers.any { it.isEmpty() })
            return PlayError.BLANK_NAME
        if(cleanPlayers.map { it.lowercase() }.distinct().size != cleanPlayers.size)
            return PlayError.DUPLICATE_NAME
        return null
    }

    fun validateWinner(winner : String?, players : List<String>) : PlayError?
    {
        val cleanWinner = cleanWinner(winner) ?: return null
        if(cleanNames(players).none { it.equals(cleanWinner, ignoreCase = true) })
            return PlayError.WINNER_NOT_A_PLAYER
        return null
    }

    fun validateDuration(durationMinutes : Int) : PlayError?
    {
        if(durationMinutes <= 0)
            return PlayError.DURATION_TOO_SHORT
        if(durationMinutes > MAX_PLAY_MINUTES)
            return PlayError.DURATION_TOO_LONG
        return null
    }

    fun validateDate(datePlayed : Long) : PlayError?
    {
        if(datePlayed > System.currentTimeMillis())
            return PlayError.DATE_IN_FUTURE
        return null
    }

    fun logPlay(gameId : Int, datePlayed : Long, durationMinutes : Int, players : List<String>, winner : String? = null) : PlayError?
    {
        if(gamesList.none { it.id == gameId })
            return PlayError.GAME_NOT_FOUND

        val error = validatePlayers(players)
            ?: validateWinner(winner, players)
            ?: validateDuration(durationMinutes)
            ?: validateDate(datePlayed)
        if(error != null)
            return error

        val cleanPlayers = cleanNames(players)
        val storedWinner = cleanWinner(winner)?.let { typed ->
            cleanPlayers.first { it.equals(typed, ignoreCase = true) }
        }
        val play = PlaySession(nextPlaySessionID(), gameId, datePlayed, durationMinutes, cleanPlayers, storedWinner)
        playSessionList.add(play)
        return null
    }

    fun playsFor(gameId : Int) : List<PlaySession>
    {
        return playSessionList.filter { it.gameId == gameId }.sortedByDescending { it.datePlayed }
    }

    fun deletePlay(playId : Int) : Boolean
    {
        return playSessionList.removeAll { it.id == playId }
    }

    private fun cleanNames(players : List<String>) : List<String>
    {
        return players.map { it.trim() }
    }

    private fun cleanWinner(winner : String?) : String?
    {
        return winner?.trim()?.takeIf { it.isNotEmpty() }
    }


}

