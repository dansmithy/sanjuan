package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.dansmithy.sanjuan.game.GameService;

public class Player {

	private String name;
	private List<Integer> hand = new ArrayList<Integer>();
	private List<Integer> buildings = new ArrayList<Integer>();
	private List<Integer> goods = new ArrayList<Integer>();

	private Integer victoryPoints;
	
	public Player(String name) {
		super();
		this.name = name;
	}

	public void moveToBuildings(Integer cardId) {
		removeHandCard(cardId);
		buildings.add(cardId);
	}
	
	public void addToHand(Integer cardId) {
		hand.add(cardId);
	}	

	public void removeHandCards(Integer[] cardIds) {
		for (Integer cardId : cardIds) {
			removeHandCard(cardId);
		}
	}
	
	private void removeHandCard(Integer cardId) {
		hand.remove(cardId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getHand() {
		return hand;
	}

	public void setHand(List<Integer> hand) {
		this.hand = hand;
	}

	public List<Integer> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Integer> town) {
		this.buildings = town;
	}

	public List<Integer> getGoods() {
		return goods;
	}

	public void setGoods(List<Integer> goods) {
		this.goods = goods;
	}
	
	public Integer getVictoryPoints() {
		return victoryPoints;
	}

	public void calculatePoints(GameService gameService) {
		List<BuildingType> buildingList = gameService.getBuildings(buildings);
		Collections.sort(buildingList, new Comparator<BuildingType>() {

			@Override
			public int compare(BuildingType first, BuildingType second) {
				return first.getComputeOrder().compareTo(second.getComputeOrder());
			}
			
		});
		
		int victoryPointsCalc = 0;
		for (BuildingType building : buildingList) {
			victoryPointsCalc += building.getVictoryPoints();
		}
		victoryPoints = victoryPointsCalc;
	}

}
