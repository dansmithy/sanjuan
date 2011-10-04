package com.github.dansmithy.sanjuan.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public class BuildingType {

	private String name;
	private int count;
	private int buildingCost;
	private int victoryPoints;
	private BuildingCategory category;
	private final Integer computeOrder;
	
	public BuildingType(String name, int count, int buildingCost,
			int victoryPoints, BuildingCategory category, Integer computeOrder) {
		super();
		this.name = name;
		this.count = count;
		this.buildingCost = buildingCost;
		this.victoryPoints = victoryPoints;
		this.category = category;
		this.computeOrder = computeOrder;
	}

	@JsonIgnore
	public String getName() {
		return name;
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
	
}
