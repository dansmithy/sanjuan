package com.github.dansmithy.sanjuan.model.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.BuildingCategory;
import com.github.dansmithy.sanjuan.model.BuildingType;

@Named
public class CardFactory {

	private Map<BuildingType.Card, BuildingType> buildingTypes = new LinkedHashMap<BuildingType.Card, BuildingType>();
	private Map<Integer, BuildingType.Card> cardMap = new HashMap<Integer, BuildingType.Card>();

	public CardFactory() {
		addBuildingTypes();
		createCardMap();
	}
	
	private void createCardMap() {
		int cardId = 1;
		for (BuildingType buildingType : buildingTypes.values()) {
			for (int count = 0; count<buildingType.getCount(); count++) {
				cardMap.put(cardId++, buildingType.getCard());
			}
		}
	}

	private void addBuildingTypes() {
		addBuildingType(new BuildingType(BuildingType.Card.IndigoPlant,10,1,1,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 indigo"));
		addBuildingType(new BuildingType(BuildingType.Card.Aqueduct,3,3,2,BuildingCategory.VIOLET,1, "aqueduct","owner produces 1 good more"));
		addBuildingType(new BuildingType(BuildingType.Card.Archive,3,1,1,BuildingCategory.VIOLET,1, "archive","owner may discard hand cards in addition to drawn cards"));
		addBuildingType(new BuildingType(BuildingType.Card.BlackMarket,3,2,1,BuildingCategory.VIOLET,1, "standard","owner may discard any 1 or 2 goods and reduce the building cost by 1 or 2 cards"));
		addBuildingType(new BuildingType(BuildingType.Card.Carpenter,3,3,2,BuildingCategory.VIOLET,1, "carpenter","owner takes 1 card from the supply after he builds a violet building"));
		addBuildingType(new BuildingType(BuildingType.Card.Chapel,3,3,2,BuildingCategory.VIOLET,1, "chapel","owner may place 1 hand card under his chapel (each worth 1 VP at game end)"));
		addBuildingType(new BuildingType(BuildingType.Card.CityHall,2,6,0,BuildingCategory.VIOLET,2, "cityhall","owner earns 1 additional point for each of his violet buildings"));
		addBuildingType(new BuildingType(BuildingType.Card.CoffeeRoaster,8,4,2,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 coffee"));
		addBuildingType(new BuildingType(BuildingType.Card.Crane,3,2,1,BuildingCategory.VIOLET,1, "standard","owner may build over one of his buildings (and pay the difference)"));
		addBuildingType(new BuildingType(BuildingType.Card.GoldMine,3,1,1,BuildingCategory.VIOLET,1, "standard","owner turns up 4 cards from the supply, if all have different building costs, he may add one to his hand"));
		addBuildingType(new BuildingType(BuildingType.Card.GuildHall,2,6,0,BuildingCategory.VIOLET,2, "guildhall","owner earns 2 additional victory points for each of his production buildings"));
		addBuildingType(new BuildingType(BuildingType.Card.Hero,3,5,5,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType(BuildingType.Card.Library,3,5,3,BuildingCategory.VIOLET,1, "library","owner uses the privilege of his role twice"));
		addBuildingType(new BuildingType(BuildingType.Card.MarketHall,3,4,2,BuildingCategory.VIOLET,1, "markethall","owner takes 1 card more for selling one good"));
		addBuildingType(new BuildingType(BuildingType.Card.MarketStand,3,2,1,BuildingCategory.VIOLET,1, "marketstand","owner takes 1 card from the supply when he sells at least 2 goods"));
		addBuildingType(new BuildingType(BuildingType.Card.Palace,2,6,0,BuildingCategory.VIOLET,3, "palace","owner earns 1 additional victory point for every 4 victory points"));
		addBuildingType(new BuildingType(BuildingType.Card.PoorHouse,3,2,1,BuildingCategory.VIOLET,1, "poorhouse","owner takes 1 card from the supply if he has only 0 or 1 hand cards after building"));
		addBuildingType(new BuildingType(BuildingType.Card.Prefecture,3,3,2,BuildingCategory.VIOLET,1, "prefecture","owner keeps 1 card more than those drawn"));
		addBuildingType(new BuildingType(BuildingType.Card.Quarry,3,4,2,BuildingCategory.VIOLET,1, "quarry","owner pays 1 card less when building a violet building"));
		addBuildingType(new BuildingType(BuildingType.Card.SilverSmelter,8,5,3,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 silver"));
		addBuildingType(new BuildingType(BuildingType.Card.Smithy,3,1,1,BuildingCategory.VIOLET,1, "smithy","owner pays 1 card less when building a production building"));
		addBuildingType(new BuildingType(BuildingType.Card.Statue,3,3,3,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType(BuildingType.Card.SugarMill,8,2,1,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 sugar"));
		addBuildingType(new BuildingType(BuildingType.Card.TobaccoStorage,8,3,2,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 tobacco"));
		addBuildingType(new BuildingType(BuildingType.Card.Tower,3,3,2,BuildingCategory.VIOLET,1, "tower","owner may have up to 12 cards in his hand"));
		addBuildingType(new BuildingType(BuildingType.Card.TradingPost,3,2,1,BuildingCategory.VIOLET,1, "tradingpost","owner may sell 1 additional good"));
		addBuildingType(new BuildingType(BuildingType.Card.TriumphalArch,2,6,0,BuildingCategory.VIOLET,2, "triumphalarch","owner earns an additional 4-6-8 victory points for 1-2-3 monuments"));
		addBuildingType(new BuildingType(BuildingType.Card.VictoryColumn,3,4,4,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType(BuildingType.Card.Well,3,2,1,BuildingCategory.VIOLET,1, "well","owner takes 1 cards from the supply when he produces at least 2 goods"));
	}
	
	private void addBuildingType(BuildingType buildingType) {
		buildingTypes.put(buildingType.getCard(), buildingType);
	}

	public List<Integer> createOrderedDeck() {
		List<Integer> deck = new ArrayList<Integer>();
		for (int count = 1; count<=cardMap.size(); count++) {
			deck.add(count);
		}
		return deck;
	}
	
	public Map<Integer, BuildingType.Card> getCardMap() {
		return Collections.unmodifiableMap(cardMap);
	}
	
	public Map<BuildingType.Card, BuildingType> getCardTypes() {
		return Collections.unmodifiableMap(buildingTypes);
	}

	public BuildingType getBuildingType(int cardId) {
		return getCardTypes().get(getCardMap().get(cardId));
	}

	public List<BuildingType> getBuildingTypes(List<Integer> buildings) {
		List<BuildingType> list = new ArrayList<BuildingType>();
		for (Integer building : buildings) {
			list.add(getBuildingType(building));
		}
		return list;
	}	
}
