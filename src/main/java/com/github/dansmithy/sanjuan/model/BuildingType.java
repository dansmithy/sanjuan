package com.github.dansmithy.sanjuan.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;

public class BuildingType {

    public enum Card {
        IndigoPlant, Aqueduct, Archive, BlackMarket, Carpenter, Chapel, CityHall, CoffeeRoaster,
        Crane, GoldMine, GuildHall, Hero, Library, MarketHall, MarketStand, Palace, PoorHouse,
        Prefecture, Quarry, SilverSmelter, Smithy, Statue, SugarMill, TobaccoStorage, Tower,
        TradingPost, TriumphalArch, VictoryColumn, Well
    }

	private Card card;
	private int count;
	private int buildingCost;
	private int victoryPoints;
	private BuildingCategory category;
	private final Integer computeOrder;
	private String processorType;
	private String description;
	
	public BuildingType(Card card, int count, int buildingCost,
			int victoryPoints, BuildingCategory category, Integer computeOrder, String processorType, String description) {
		super();
		this.card = card;
		this.count = count;
		this.buildingCost = buildingCost;
		this.victoryPoints = victoryPoints;
		this.category = category;
		this.computeOrder = computeOrder;
		this.processorType = processorType;
		this.description = description;
	}

	@JsonIgnore
	public Card getCard() {
		return card;
	}

	@JsonIgnore
	public int getCount() {
		return count;
	}

	@JsonIgnore
	public Integer getComputeOrder() {
		return computeOrder;
	}

	public int getBuildingCost() {
		return buildingCost;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public BuildingCategory getCategory() {
		return category;
	}
	
	public String getDescription() {
		return description;
	}

	@JsonIgnore
	public String getProcessorType() {
		return processorType;
	}
	
	@JsonIgnore
	public boolean isProductionBuilding() {
		return category.equals(BuildingCategory.PRODUCTION);
	}
	
	@JsonIgnore
	public boolean isVioletBuilding() {
		return !isProductionBuilding();
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	
}
