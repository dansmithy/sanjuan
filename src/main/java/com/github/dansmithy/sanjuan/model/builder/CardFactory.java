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
		addBuildingType(new BuildingType("Aqueduct",3,3,2,BuildingCategory.VIOLET, 1, "aqueduct"));
		addBuildingType(new BuildingType("Archive",3,1,1,BuildingCategory.VIOLET, 1, "archive"));
		addBuildingType(new BuildingType("BlackMarket",3,2,1,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("Carpenter",3,3,2,BuildingCategory.VIOLET, 1, "carpenter"));
		addBuildingType(new BuildingType("Chapel",3,3,2,BuildingCategory.VIOLET, 1, "chapel"));
		addBuildingType(new BuildingType("CityHall",2,6,0,BuildingCategory.VIOLET, 2, "cityhall"));
		addBuildingType(new BuildingType("CoffeeRoaster",8,4,2,BuildingCategory.PRODUCTION, 1, "standard"));
		addBuildingType(new BuildingType("Crane",3,2,1,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("GoldMine",3,1,1,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("GuildHall",2,6,0,BuildingCategory.VIOLET, 2, "guildhall"));
		addBuildingType(new BuildingType("Hero",3,5,5,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("Library",3,5,3,BuildingCategory.VIOLET, 1, "library"));
		addBuildingType(new BuildingType("MarketHall",3,4,2,BuildingCategory.VIOLET, 1, "markethall"));
		addBuildingType(new BuildingType("MarketStand",3,2,1,BuildingCategory.VIOLET, 1, "marketstand"));
		addBuildingType(new BuildingType("Palace",2,6,0,BuildingCategory.VIOLET, 3, "palace"));
		addBuildingType(new BuildingType("PoorHouse",3,2,1,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("Prefecture",3,3,2,BuildingCategory.VIOLET, 1, "prefecture"));
		addBuildingType(new BuildingType("Quarry",3,4,2,BuildingCategory.VIOLET, 1, "quarry"));
		addBuildingType(new BuildingType("SilverSmelter",8,5,3,BuildingCategory.PRODUCTION, 1, "standard"));
		addBuildingType(new BuildingType("Smithy",3,1,1,BuildingCategory.VIOLET, 1, "smithy"));
		addBuildingType(new BuildingType("Statue",3,3,3,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("SugarMill",8,2,1,BuildingCategory.PRODUCTION, 1, "standard"));
		addBuildingType(new BuildingType("TobaccoStorage",8,3,2,BuildingCategory.PRODUCTION, 1, "standard"));
		addBuildingType(new BuildingType("Tower",3,3,2,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("TradingPost",3,2,1,BuildingCategory.VIOLET, 1, "tradingpost"));
		addBuildingType(new BuildingType("TriumphalArch",2,6,0,BuildingCategory.VIOLET, 2, "triumphalarch"));
		addBuildingType(new BuildingType("VictoryColumn",3,4,4,BuildingCategory.VIOLET, 1, "standard"));
		addBuildingType(new BuildingType("Well",3,2,1,BuildingCategory.VIOLET, 1, "well"));		
		addBuildingType(new BuildingType("IndigoPlant",10,1,1,BuildingCategory.PRODUCTION, 1, "standard"));
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

	public List<BuildingType> getBuildings(List<Integer> buildings) {
		List<BuildingType> list = new ArrayList<BuildingType>();
		for (Integer building : buildings) {
			list.add(getBuildingType(building));
		}
		return list;
	}	
}
