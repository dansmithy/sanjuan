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

	private Map<String, BuildingType> buildingTypes = new LinkedHashMap<String, BuildingType>();
	private Map<Integer, String> cardMap = new HashMap<Integer, String>();

	public CardFactory() {
		addBuildingTypes();
		createCardMap();
	}
	
	private void createCardMap() {
		int cardId = 1;
		for (BuildingType buildingType : buildingTypes.values()) {
			for (int count = 0; count<buildingType.getCount(); count++) {
				cardMap.put(cardId++, buildingType.getName());
			}
		}
	}

	private void addBuildingTypes() {
		addBuildingType(new BuildingType("IndigoPlant",10,1,1,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 indigo"));
		addBuildingType(new BuildingType("Aqueduct",3,3,2,BuildingCategory.VIOLET,1, "aqueduct","owner produces 1 good more"));
		addBuildingType(new BuildingType("Archive",3,1,1,BuildingCategory.VIOLET,1, "archive","owner may discard hand cards in addition to drawn cards"));
		addBuildingType(new BuildingType("BlackMarket",3,2,1,BuildingCategory.VIOLET,1, "standard","owner may discard any 1 or 2 goods and reduce the building cost by 1 or 2 cards"));
		addBuildingType(new BuildingType("Carpenter",3,3,2,BuildingCategory.VIOLET,1, "carpenter","owner takes 1 card from the supply after he builds a violet building"));
		addBuildingType(new BuildingType("Chapel",3,3,2,BuildingCategory.VIOLET,1, "chapel","owner may place 1 hand card under his chapel (each worth 1 VP at game end)"));
		addBuildingType(new BuildingType("CityHall",2,6,0,BuildingCategory.VIOLET,2, "cityhall","owner earns 1 additional point for each of his violet buildings"));
		addBuildingType(new BuildingType("CoffeeRoaster",8,4,2,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 coffee"));
		addBuildingType(new BuildingType("Crane",3,2,1,BuildingCategory.VIOLET,1, "standard","owner may build over one of his buildings (and pay the difference)"));
		addBuildingType(new BuildingType("GoldMine",3,1,1,BuildingCategory.VIOLET,1, "standard","owner turns up 4 cards from the supply, if all have different building costs, he may add one to his hand"));
		addBuildingType(new BuildingType("GuildHall",2,6,0,BuildingCategory.VIOLET,2, "guildhall","owner earns 2 additional victory points for each of his production buildings"));
		addBuildingType(new BuildingType("Hero",3,5,5,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType("Library",3,5,3,BuildingCategory.VIOLET,1, "library","owner uses the privilege of his role twice"));
		addBuildingType(new BuildingType("MarketHall",3,4,2,BuildingCategory.VIOLET,1, "markethall","owner takes 1 card more for selling one good"));
		addBuildingType(new BuildingType("MarketStand",3,2,1,BuildingCategory.VIOLET,1, "marketstand","owner takes 1 card from the supply when he sells at least 2 goods"));
		addBuildingType(new BuildingType("Palace",2,6,0,BuildingCategory.VIOLET,3, "palace","owner earns 1 additional victory point for every 4 victory points"));
		addBuildingType(new BuildingType("PoorHouse",3,2,1,BuildingCategory.VIOLET,1, "poorhouse","owner takes 1 card from the supply if he has only 0 or 1 hand cards after building"));
		addBuildingType(new BuildingType("Prefecture",3,3,2,BuildingCategory.VIOLET,1, "prefecture","owner keeps 1 card more than those drawn"));
		addBuildingType(new BuildingType("Quarry",3,4,2,BuildingCategory.VIOLET,1, "quarry","owner pays 1 card less when building a violet building"));
		addBuildingType(new BuildingType("SilverSmelter",8,5,3,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 silver"));
		addBuildingType(new BuildingType("Smithy",3,1,1,BuildingCategory.VIOLET,1, "smithy","owner pays 1 card less when building a production building"));
		addBuildingType(new BuildingType("Statue",3,3,3,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType("SugarMill",8,2,1,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 sugar"));
		addBuildingType(new BuildingType("TobaccoStorage",8,3,2,BuildingCategory.PRODUCTION,1, "standard","owner produces 1 tobacco"));
		addBuildingType(new BuildingType("Tower",3,3,2,BuildingCategory.VIOLET,1, "tower","owner may have up to 12 cards in his hand"));
		addBuildingType(new BuildingType("TradingPost",3,2,1,BuildingCategory.VIOLET,1, "tradingpost","owner may sell 1 additional good"));
		addBuildingType(new BuildingType("TriumphalArch",2,6,0,BuildingCategory.VIOLET,2, "triumphalarch","owner earns an additional 4-6-8 victory points for 1-2-3 monuments"));
		addBuildingType(new BuildingType("VictoryColumn",3,4,4,BuildingCategory.VIOLET,1, "standard","monument (no special function)"));
		addBuildingType(new BuildingType("Well",3,2,1,BuildingCategory.VIOLET,1, "well","owner takes 1 cards from the supply when he produces at least 2 goods"));
	}
	
	private void addBuildingType(BuildingType buildingType) {
		buildingTypes.put(buildingType.getName(), buildingType);
	}

	public List<Integer> createOrderedDeck() {
		List<Integer> deck = new ArrayList<Integer>();
		for (int count = 1; count<=cardMap.size(); count++) {
			deck.add(count);
		}
		return deck;
	}
	
	public Map<Integer, String> getCardMap() {
		return Collections.unmodifiableMap(cardMap);
	}
	
	public Map<String, BuildingType> getCardTypes() {
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
