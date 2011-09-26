package com.github.dansmithy.sanjuan.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public class BuildingType {

	private String name;
	private int count;
	private int buildingCost;
	private int victoryPoints;
	private BuildingCategory category;
	
	public BuildingType(String name, int count, int buildingCost,
			int victoryPoints, BuildingCategory category) {
		super();
		this.name = name;
		this.count = count;
		this.buildingCost = buildingCost;
		this.victoryPoints = victoryPoints;
		this.category = category;
	}

	@JsonIgnore
	public String getName() {
		return name;
	}

	@JsonIgnore
	public int getCount() {
		return count;
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
	
}
