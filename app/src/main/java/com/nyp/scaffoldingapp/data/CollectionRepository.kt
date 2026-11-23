package com.nyp.scaffoldingapp.data

import android.R
import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.GameRef
import com.nyp.scaffoldingapp.data.model.Membership
import com.nyp.scaffoldingapp.data.model.CollectionType

import java.util.Calendar
class CollectionRepository {

    private val collectionList : MutableList<GameCollection> = mutableListOf<GameCollection>()
    private val gamesList : MutableList<GameRef> = mutableListOf<GameRef>()
    private val membershipList : MutableList<Membership> = mutableListOf<Membership>()
    private var collectionIDCounter : Int = 0

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
        if(isNameTaken(name) || name.isBlank())
            return false
        val customCollection = GameCollection(nextCollectionID(), name.trim(), CollectionType.Custom)
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
            if(collection.status != CollectionType.Custom)
                return true
            return false
        return false
    }

    public fun isNameTaken(name : String) : Boolean
    {
        val result = name.filterNot{it.isWhitespace()}.lowercase()
        val bool = collectionList.any{it.name.filterNot{it.isWhitespace()}.lowercase() == result}
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
}

