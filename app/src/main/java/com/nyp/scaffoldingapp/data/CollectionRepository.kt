package com.nyp.scaffoldingapp.data

import com.nyp.scaffoldingapp.data.model.CollectionData
import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.GameRef
import com.nyp.scaffoldingapp.data.model.Membership
import com.nyp.scaffoldingapp.data.model.PlaySession
import com.nyp.scaffoldingapp.data.model.CollectionType
import com.nyp.scaffoldingapp.data.model.NameError
import com.nyp.scaffoldingapp.data.model.PlayError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

fun CollectionData.gamesIn(collectionId: Int): List<GameRef> {
    val gameIds = membership.filter{it.collectionID == collectionId}.map{it.gameID}.toSet()
    return games.filter{it.id in gameIds}
}

fun CollectionData.collectionsContaining(gameID : Int) : List<GameCollection>
{
    val listOfCollectionIDs = membership.filter{it.gameID == gameID}.map{it.collectionID}.toSet()
    return collection.filter{it.id in listOfCollectionIDs}
}
class CollectionRepository {

    private val collectionState = MutableStateFlow(CollectionData())
    val data: StateFlow<CollectionData> = collectionState.asStateFlow()
    private var collectionIDCounter : Int = 0
    private var playSessionIDCounter : Int = 0
    companion object
    {
        const val MAX_PLAY_MINUTES = 1440
    }

    init {
        val wishlist = GameCollection(nextCollectionID(), "Wishlist", CollectionType.Wishlist)
        val owned = GameCollection(nextCollectionID(), "Owned", CollectionType.Owned)
        collectionState.update { it.copy(collection = listOf(wishlist, owned)) }
    }

    fun createCollection(name : String) : NameError? {
        val result = validateCollectionName(name)
        if(result != null)
            return result

        val customCollection = GameCollection(nextCollectionID(), name.trim(), CollectionType.Custom)
        collectionState.update { it.copy(collection = it.collection + customCollection) }
        return null

    }

    fun deleteCollection(id : Int) {
        if(!isBuiltIn(id))
        {
            collectionState.update{current -> current.copy(
                membership = current.membership.filterNot{it.collectionID == id},
                collection = current.collection.filterNot{it.id == id})}
        }
        return
    }

    fun addGameToCollection(game: GameRef, collectionID: Int) {
        if (isMember(collectionID, game.id))
            return

        val wishlistId = collectionOfType(CollectionType.Wishlist)?.id
        val addingToOwned = collectionOfType(CollectionType.Owned)?.id == collectionID
        val newMembership = Membership(collectionID, game.id, System.currentTimeMillis())

        collectionState.update { current ->
            val games = if (current.games.any { it.id == game.id })
                current.games
            else
                current.games + game
            val memberships = current.membership.filterNot { addingToOwned && it.collectionID == wishlistId && it.gameID == game.id } + newMembership
            current.copy(games = games, membership = memberships)
        }
    }

    fun removeGameFromCollection(gameID : Int, collectionID : Int){

        collectionState.update{current -> current.copy(
            membership = current.membership.filterNot{it.collectionID == collectionID && it.gameID == gameID})}
    }

    private fun isMember(collectionID : Int, gameID : Int) : Boolean
    {

        val membershipMatch = collectionState.value.membership.any{it.collectionID == collectionID && it.gameID == gameID}
        return membershipMatch
    }

    private fun collectionOfType(type : CollectionType) : GameCollection?
    {
        val collection = collectionState.value.collection.find{it.type == type}
        return collection
    }

    fun isBuiltIn(collectionID : Int) : Boolean
    {
        val collection = collectionState.value.collection.find{it.id == collectionID}
        return collection != null && (collection.type == CollectionType.Wishlist || collection.type == CollectionType.Owned)
    }

    private fun isNameTaken(name : String, id : Int?= null) : Boolean
    {
        val result = name.filterNot{it.isWhitespace()}.lowercase()
        val list = collectionState.value.collection.filterNot{it.id == id}
        val bool = list.any{it -> it.name.filterNot{it.isWhitespace()}.lowercase() == result}
        return bool
    }

    private fun nextCollectionID() : Int
    {
        val counter = collectionIDCounter
        collectionIDCounter += 1
        return counter
    }



    fun findCollection(id : Int) : GameCollection? {
        return collectionState.value.collection.find{it.id == id}
    }

    fun gameCountInCollection(id : Int) : Int {
        return collectionState.value.gamesIn(id).count()
    }

    fun renameCollection(id: Int, name : String) : Boolean {
        if(isBuiltIn(id))
            return false
        if(validateCollectionName(name, id) != null)
            return false
        val index = collectionState.value.collection.indexOfFirst{it.id == id}
        if(index != -1)
        {
            collectionState.update{it.copy(collection = it.collection.map {c ->
                if(c.id == id)
                c.copy(name = name.trim())
            else
                c})}
            return true
        }
        return false
    }

    fun validateCollectionName(name : String, id : Int?= null) : NameError? {
        val trim = name.trim()

        if(trim.isBlank())
            return NameError.BLANK

        if(isNameTaken(trim, id))
            return NameError.TAKEN

        return null
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

        if(cleanPlayers.any{it.isEmpty()})
            return PlayError.BLANK_NAME

        if(cleanPlayers.map{it.lowercase()}.distinct().size != cleanPlayers.size)
            return PlayError.DUPLICATE_NAME

        return null
    }

    fun validateWinner(winner : String?, players : List<String>) : PlayError?
    {
        val cleanWinner = cleanWinner(winner) ?: return null
        if(cleanNames(players).none{it.equals(cleanWinner, ignoreCase = true)})
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
        if(collectionState.value.games.none{it.id == gameId})
            return PlayError.GAME_NOT_FOUND

        val error = validatePlayers(players)
            ?: validateWinner(winner, players)
            ?: validateDuration(durationMinutes)
            ?: validateDate(datePlayed)

        if(error != null)
            return error

        val cleanPlayers = cleanNames(players)
        val storedWinner = cleanWinner(winner)?.let {typed ->
            cleanPlayers.first {it.equals(typed, ignoreCase = true) }
        }
        val play = PlaySession(nextPlaySessionID(), gameId, datePlayed, durationMinutes, cleanPlayers, storedWinner)
        collectionState.update{it.copy(playSessions = it.playSessions + play)}
        return null
    }

    fun playsFor(gameId : Int) : List<PlaySession>
    {
        return collectionState.value.playSessions.filter{it.gameId == gameId}.sortedByDescending { it.datePlayed }
    }

    fun deletePlay(playId : Int) : Boolean
    {
        val playValid = collectionState.value.playSessions.any{it.id == playId}
        collectionState.update{it.copy(playSessions = it.playSessions.filterNot{it.id == playId})}
        return playValid
    }

    private fun cleanNames(players : List<String>) : List<String>
    {
        return players.map {it.trim()}
    }

    private fun cleanWinner(winner : String?) : String?
    {
        return winner?.trim()?.takeIf{it.isNotEmpty()}
    }


}

