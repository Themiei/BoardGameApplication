package com.nyp.scaffoldingapp.data

import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.GameRef
import com.nyp.scaffoldingapp.data.model.Membership
import com.nyp.scaffoldingapp.data.model.CollectionType

class CollectionRepository {

    private val collectionList : MutableList<GameCollection> = mutableListOf<GameCollection>()
    private val gamesList : MutableList<GameRef> = mutableListOf<GameRef>()
    private val membershipList : MutableList<Membership> = mutableListOf<Membership>()

    public fun ReadCollections(): MutableList<GameCollection> {
        return collectionList
    }

    public fun ReadGames() : List<GameRef> {
        return gamesList
    }

    public fun ReadMemberships() : List<Membership> {
        return membershipList
    }

    public fun Init() {
        val wishlist = GameCollection(0, "WishList", CollectionType.Wishlist)
        val owned = GameCollection(1, "Owned", CollectionType.Owned)
        collectionList.add(wishlist)
        collectionList.add(owned)
    }

    public fun CreateCollection(name : String) {
        val customCollection = GameCollection(collectionList.lastIndex + 1, name, CollectionType.Custom)
        if(collectionList.find{it.name == name} != null)
            return
        collectionList.add(customCollection)
        return

    }

    public fun DeleteCollection(collectionEntry : GameCollection) {
        collectionList.remove(collectionEntry)
        val collectionID = collectionEntry.id
        membershipList.removeAll{it.collectionID == collectionID}
        return
    }

    public fun AddGameToCollection(gameEntry : GameRef, collectionEntry: GameCollection) {
        if(membershipList.find{it.collectionID == collectionEntry.id; it.gameID == gameEntry.id} != null)
            return
        if(collectionEntry.status == CollectionType.Owned)
            membershipList.removeAll{it.collectionID == collectionEntry.id; it.gameID == gameEntry.id}
        val newMembership = Membership(collectionEntry.id, gameEntry.id, )
        membershipList.add()
    }
}