package com.github.dansmithy.at;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.GivenBddParts;
import com.github.dansmithy.driver.DeckOrder;
import com.github.dansmithy.driver.GameDriver;
import org.junit.Test;

import static com.github.dansmithy.bdd.BddHelper.then;
import static com.github.dansmithy.bdd.BddHelper.when;
import static com.github.dansmithy.bdd.GivenBddParts.given;
import static com.github.dansmithy.driver.BddPartProvider.*;

public class LibraryAT extends BaseAT {

	@Test
	public void testCanChooseAndUseLibraryOnFirstRoleChoice() {

		bdd.runTest(

				given(anyNumberOfTwitterMessagesPermitted())
                        .and(gameBegunWithTwoPlayers("#alice", "#bob", DeckOrder.Order5))
                        .and(roundsOneAndTwo())
                ,

				when(roundThreeLibraryUsedOnFirstPhase()),

				then(verifySuccessfulResponseContains("{ 'roundNumber' : 3, 'players^name' : [ { 'name' : '#alice', handCount : 3 } ] }")));
	}


    @Test
    public void testLibraryRequestWillBeIgnoredIfRequestedTwice() {
        bdd.runTest(

                given(anyNumberOfTwitterMessagesPermitted())
                        .and(gameBegunWithTwoPlayers("#alice", "#bob", DeckOrder.Order5))
                        .and(roundsOneAndTwo())
                ,

                when(roundThreeLibraryRequestedTwice()),

                then(verifySuccessfulResponseContains("{ 'roundNumber' : 3, 'players^name' : [ { 'name' : '#alice', handCount : 3 } ] }")));

    }

    @Test
    public void testCannotUseLibraryIfNotChosenOnFirstRoleChoice() {

    }

    @Test
    public void testUseLibrarySecondTimeIfNotUsedFirstTime() {
        bdd.runTest(

                given(anyNumberOfTwitterMessagesPermitted())
                        .and(gameBegunWithTwoPlayers("#alice", "#bob", DeckOrder.Order5))
                        .and(roundsOneAndTwo())
                ,

                when(roundThreeLibraryUsedOnSecondPhase()),

                then(verifySuccessfulResponseContains("{ 'roundNumber' : 3, 'players^name' : [ { 'name' : '#alice', handCount : 3 } ] }")));

    }

    private BddPart<GameDriver> roundsOneAndTwo() {

        return new GivenBddParts(
                roleChosenBy("#alice", "round : 1; phase : 1", "role : BUILDER"))
                .and(userPlays("#alice", "round : 1; phase : 1; play : 1", "{  build : '#library', payment : [ '#prefecture', '#indigoplant3', '#indigoplant4', '#silversmelter7' ]  }"))
                .and(userPlays("#bob", "round : 1; phase : 1; play : 2", "{  build : '#coffeeroaster', payment : [ '#indigoplant7', '#indigoplant8', '#smithy', '#indigoplant6' ]  }"))
                .and(roleChosenBy("#bob", "round : 1; phase : 2", "role : PROSPECTOR"))
                .and(userPlays("#bob", "round : 1; phase : 2; play : 1", "{  }"))
                .and(userPlays("#alice", "round : 1; phase : 2; play : 2", "{  }"))
                .and(roleChosenBy("#alice", "round : 1; phase : 3", "role : COUNCILLOR")) // no need to choose library as haven't used yet this round. library should give 8 cards offered
                .and(userPlays("#alice", "round : 1; phase : 3; play : 1", "{  councilDiscarded : [ '#indigoplant9', '#indigoplant10', '#sugarmill', '#sugarmill2', '#sugarmill3', '#sugarmill4', '#sugarmill5' ]  }"))
                .and(userPlays("#bob", "round : 1; phase : 3; play : 2", "{  councilDiscarded : [ '#sugarmill8' ]  }"))
                .and(roleChosenBy("#bob", "round : 2; phase : 1", "role : PRODUCER"))
                .and(userPlays("#bob", "round : 2; phase : 1; play : 1", "{  productionFactories : [ '#coffeeroaster', '#indigoplant2' ]  }"))
                .and(userPlays("#alice", "round : 2; phase : 1; play : 2", "{  productionFactories : [ '#indigoplant' ]  }"))
                .and(roleChosenBy("#alice", "round : 2; phase : 2", "role : TRADER")) // only go this round, so library in use. however, only have one good to trade.
                .and(userPlays("#alice", "round : 2; phase : 2; play : 1", "{  productionFactories : [ '#indigoplant' ]  }"))
                .and(userPlays("#bob", "round : 2; phase : 2; play : 2", "{  productionFactories : [ '#coffeeroaster' ]  }"))
                .and(roleChosenBy("#bob", "round : 2; phase : 3", "role : COUNCILLOR"))
                .and(userPlays("#bob", "round : 2; phase : 3; play : 1", "{  councilDiscarded : [ '#aqueduct', '#coffeeroaster2', '#coffeeroaster3', '#coffeeroaster4' ]  }"))
                .and(userPlays("#alice", "round : 2; phase : 3; play : 2", "{  councilDiscarded : [ '#coffeeroaster6' ]  }"));
    }


    private BddPart<GameDriver> roundThreeLibraryUsedOnFirstPhase() {

        return new GivenBddParts(
                roleChosenBy("#alice", "round : 3; phase : 1", String.format("role : BUILDER; useLibrary : true"))) // choose library! library makes cost 0
                .and(userPlays("#alice", "round : 3; phase : 1; play : 1", "{  build : '#sugarmill6', payment : [  ]  }"))
                .and(userPlays("#bob", "round : 3; phase : 1; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#bob", "round : 3; phase : 2", "role : PRODUCER"))
                .and(userPlays("#bob", "round : 3; phase : 2; play : 1", "{  productionFactories : [ '#coffeeroaster' ]  }"))
                .and(userPlays("#alice", "round : 3; phase : 2; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#alice", "round : 3; phase : 3", "role : PROSPECTOR"))
                .and(userPlays("#alice", "round : 3; phase : 3; play : 1", "{  }")) // already used library, so should get one extra card (not two)
        ;

    }

    private BddPart<GameDriver> roundThreeLibraryUsedOnSecondPhase() {

        return new GivenBddParts(
                roleChosenBy("#alice", "round : 3; phase : 1", String.format("role : BUILDER; useLibrary : false")))
                .and(userPlays("#alice", "round : 3; phase : 1; play : 1", "{  build : '#sugarmill6', payment : [ '#tobaccostorage4'  ]  }"))
                .and(userPlays("#bob", "round : 3; phase : 1; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#bob", "round : 3; phase : 2", "role : PRODUCER"))
                .and(userPlays("#bob", "round : 3; phase : 2; play : 1", "{  productionFactories : [ '#coffeeroaster' ]  }"))
                .and(userPlays("#alice", "round : 3; phase : 2; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#alice", "round : 3; phase : 3", "role : PROSPECTOR"))
                .and(userPlays("#alice", "round : 3; phase : 3; play : 1", "{  }")) // haven't already used library, so should get two extra cards

                ;

    }

    private BddPart<GameDriver> roundThreeLibraryRequestedTwice() {

        return new GivenBddParts(
                roleChosenBy("#alice", "round : 3; phase : 1", String.format("role : BUILDER; useLibrary : true"))) // choose library! library makes cost 0
                .and(userPlays("#alice", "round : 3; phase : 1; play : 1", "{  build : '#sugarmill6', payment : [  ]  }"))
                .and(userPlays("#bob", "round : 3; phase : 1; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#bob", "round : 3; phase : 2", "role : PRODUCER"))
                .and(userPlays("#bob", "round : 3; phase : 2; play : 1", "{  productionFactories : [ '#coffeeroaster' ]  }"))
                .and(userPlays("#alice", "round : 3; phase : 2; play : 2", "{  skip : true  }"))
                .and(roleChosenBy("#alice", "round : 3; phase : 3", "role : PROSPECTOR; useLibrary : true")) // shouldn't be allowed to do this: should be ignored!
                .and(userPlays("#alice", "round : 3; phase : 3; play : 1", "{  }")) // already used library, so should get one extra card (not two)
                ;

    }


}
