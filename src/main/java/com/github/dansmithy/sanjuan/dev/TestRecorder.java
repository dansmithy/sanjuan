package com.github.dansmithy.sanjuan.dev;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.github.dansmithy.sanjuan.model.input.GovernorChoice;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.RoleChoice;
import com.github.dansmithy.sanjuan.security.user.AuthenticatedUser;

public class TestRecorder {

	private static boolean enabled = false;
	
	private static String[] QUICK_NAMES = { "NONE",
			"#indigoplant",
			"#indigoplant2",
			"#indigoplant3",
			"#indigoplant4",
			"#indigoplant5",
			"#indigoplant6",
			"#indigoplant7",
			"#indigoplant8",
			"#indigoplant9",
			"#indigoplant10",
			"#aqueduct",
			"#aqueduct2",
			"#aqueduct3",
			"#archive",
			"#archive2",
			"#archive3",
			"#blackmarket",
			"#blackmarket2",
			"#blackmarket3",
			"#carpenter",
			"#carpenter2",
			"#carpenter3",
			"#chapel",
			"#chapel2",
			"#chapel3",
			"#cityhall",
			"#cityhall2",
			"#coffeeroaster",
			"#coffeeroaster2",
			"#coffeeroaster3",
			"#coffeeroaster4",
			"#coffeeroaster5",
			"#coffeeroaster6",
			"#coffeeroaster7",
			"#coffeeroaster8",
			"#crane",
			"#crane2",
			"#crane3",
			"#goldmine",
			"#goldmine2",
			"#goldmine3",
			"#guildhall",
			"#guildhall2",
			"#hero",
			"#hero2",
			"#hero3",
			"#library",
			"#library2",
			"#library3",
			"#markethall",
			"#markethall2",
			"#markethall3",
			"#marketstand",
			"#marketstand2",
			"#marketstand3",
			"#palace",
			"#palace2",
			"#poorhouse",
			"#poorhouse2",
			"#poorhouse3",
			"#prefecture",
			"#prefecture2",
			"#prefecture3",
			"#quarry",
			"#quarry2",
			"#quarry3",
			"#silversmelter",
			"#silversmelter2",
			"#silversmelter3",
			"#silversmelter4",
			"#silversmelter5",
			"#silversmelter6",
			"#silversmelter7",
			"#silversmelter8",
			"#smithy",
			"#smithy2",
			"#smithy3",
			"#statue",
			"#statue2",
			"#statue3",
			"#sugarmill",
			"#sugarmill2",
			"#sugarmill3",
			"#sugarmill4",
			"#sugarmill5",
			"#sugarmill6",
			"#sugarmill7",
			"#sugarmill8",
			"#tobaccostorage",
			"#tobaccostorage2",
			"#tobaccostorage3",
			"#tobaccostorage4",
			"#tobaccostorage5",
			"#tobaccostorage6",
			"#tobaccostorage7",
			"#tobaccostorage8",
			"#tower",
			"#tower2",
			"#tower3",
			"#tradingpost",
			"#tradingpost2",
			"#tradingpost3",
			"#triumphalarch",
			"#triumphalarch2",
			"#victorycolumn",
			"#victorycolumn2",
			"#victorycolumn3",
			"#well",
			"#well2",
			"#well3"
	};
	                           
	
	public static String deckShorthand() {
		return "#indigoplant : 1; #indigoplant2 : 2; #indigoplant3 : 3; #indigoplant4 : 4; #indigoplant5 : 5; #indigoplant6 : 6; #indigoplant7 : 7; #indigoplant8 : 8; #indigoplant9 : 9; #indigoplant10 : 10; #aqueduct : 11; #aqueduct2 : 12; #aqueduct3 : 13; #archive : 14; #archive2 : 15; #archive3 : 16; #blackmarket : 17; #blackmarket2 : 18; #blackmarket3 : 19; #carpenter : 20; #carpenter2 : 21; #carpenter3 : 22; #chapel : 23; #chapel2 : 24; #chapel3 : 25; #cityhall : 26; #cityhall2 : 27; #coffeeroaster : 28; #coffeeroaster2 : 29; #coffeeroaster3 : 30; #coffeeroaster4 : 31; #coffeeroaster5 : 32; #coffeeroaster6 : 33; #coffeeroaster7 : 34; #coffeeroaster8 : 35; #crane : 36; #crane2 : 37; #crane3 : 38; #goldmine : 39; #goldmine2 : 40; #goldmine3 : 41; #guildhall : 42; #guildhall2 : 43; #hero : 44; #hero2 : 45; #hero3 : 46; #library : 47; #library2 : 48; #library3 : 49; #markethall : 50; #markethall2 : 51; #markethall3 : 52; #marketstand : 53; #marketstand2 : 54; #marketstand3 : 55; #palace : 56; #palace2 : 57; #poorhouse : 58; #poorhouse2 : 59; #poorhouse3 : 60; #prefecture : 61; #prefecture2 : 62; #prefecture3 : 63; #quarry : 64; #quarry2 : 65; #quarry3 : 66; #silversmelter : 67; #silversmelter2 : 68; #silversmelter3 : 69; #silversmelter4 : 70; #silversmelter5 : 71; #silversmelter6 : 72; #silversmelter7 : 73; #silversmelter8 : 74; #smithy : 75; #smithy2 : 76; #smithy3 : 77; #statue : 78; #statue2 : 79; #statue3 : 80; #sugarmill : 81; #sugarmill2 : 82; #sugarmill3 : 83; #sugarmill4 : 84; #sugarmill5 : 85; #sugarmill6 : 86; #sugarmill7 : 87; #sugarmill8 : 88; #tobaccostorage : 89; #tobaccostorage2 : 90; #tobaccostorage3 : 91; #tobaccostorage4 : 92; #tobaccostorage5 : 93; #tobaccostorage6 : 94; #tobaccostorage7 : 95; #tobaccostorage8 : 96; #tower : 97; #tower2 : 98; #tower3 : 99; #tradingpost : 100; #tradingpost2 : 101; #tradingpost3 : 102; #triumphalarch : 103; #triumphalarch2 : 104; #victorycolumn : 105; #victorycolumn2 : 106; #victorycolumn3 : 107; #well : 108; #well2 : 109; #well3 : 110";
	}
	
	
	public static void outputPlay(Integer roundNumber,
			Integer phaseNumber, Integer playNumber, PlayChoice playChoice) {
		
		if (enabled) {
			String choice = createChoice(playChoice);
			String play = String.format(".and(userPlays(\"#%s\", \"round : %d; phase : %d; play : %d\", \"{ %s }\"))", trimmedUser(), roundNumber, phaseNumber, playNumber, choice);
			output(play);
		}
	}
	
	public static void roleChosenBy(Integer roundNumber,
			Integer phaseNumber, RoleChoice choice) {
		if (enabled) {
			String play = String.format(".and(roleChosenBy(\"#%s\", \"round : %d; phase : %d\", \"role : %s\"))", trimmedUser(), roundNumber, phaseNumber, choice.getRole().toString());
			output(play);
		}		
	}
	
	public static void governorPlay(Integer roundNumber,
			GovernorChoice governorChoice) {
		if (enabled) {
			String govChoice = createGovernorChoice(governorChoice);
			String play = String.format(".and(userMakesGovernorPlay(\"#%s\", \"round : %d\", \"{ %s }\"))", trimmedUser(), roundNumber, govChoice);
			output(play);
		}		
	}	

	private static String createGovernorChoice(GovernorChoice governorChoice) {
		String result = "";
		if (!governorChoice.getCardsToDiscard().isEmpty()) {
			result += String.format("'cardsToDiscard' : [ %s ]", translateArray(governorChoice.getCardsToDiscard()));
		}
		if (governorChoice.getChapelCard() != null) {
			if (!result.equals("")) {
				result += ", ";
			}
			result += String.format("'chapelCard' : %s", translateCard(governorChoice.getChapelCard()));
		}
		return result;
	}


	private static String trimmedUser() {
		String user = AuthenticatedUser.get();
		return user.substring(0, user.indexOf("-"));
	}


	private static void output(String play) {
		System.out.println(play);
	}


	private static String createChoice(PlayChoice playChoice) {
		if (playChoice.getBuild() != null) {
			return createBuild(playChoice);
		}
		if (playChoice.getCouncilDiscarded() != null) {
			return createCouncil(playChoice);
		}
		if (playChoice.getProductionFactories() != null) {
			return createProduceOrTrade(playChoice);
		}
		if (playChoice.getSkip() != null) {
			return createSkip(playChoice);
		}
		return "";
	}


	private static String createSkip(PlayChoice playChoice) {
		return " skip : true ";
	}

	private static String createProduceOrTrade(PlayChoice playChoice) {
		return String.format(" productionFactories : [ %s ] ", translateArray(playChoice.getProductionFactories()));
	}


	private static String createCouncil(PlayChoice playChoice) {
		return String.format(" councilDiscarded : [ %s ] ", translateArray(playChoice.getCouncilDiscarded()));
	}


	private static String createBuild(PlayChoice playChoice) {
		return String.format(" build : %s, payment : [ %s ] ", translateCard(playChoice.getBuild()), translateArray(playChoice.getPayment()));
	}


	private static String translateArray(List<Integer> payment) {
		String[] cards = new String[payment.size()];
		for (int i=0; i<payment.size(); i++) {
			cards[i] = translateCard(payment.get(i));
		}
		return StringUtils.join(cards, ", ");
	}


	private static String translateCard(Integer card) {
		return "'" + QUICK_NAMES[card] + "'";
	}


}
