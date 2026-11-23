package com.nyp.scaffoldingapp.data

import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.GameRef
import com.nyp.scaffoldingapp.data.model.Membership

class CollectionRepository {

    private val collectionList : List<GameCollection> = mutableListOf<GameCollection>();
    private val gamesList : List<GameRef> = mutableListOf<GameRef>();
    private val membershipList : List<Membership> = mutableListOf<Membership>();

    public fun ReadCollections(): List<GameCollection> {
        return collectionList;
    }

    public fun ReadGames() : List<GameRef> {
        return gamesList;
    }

    public fun ReadMemberships() : List<Membership> {
        return membershipList;
    }

    
}