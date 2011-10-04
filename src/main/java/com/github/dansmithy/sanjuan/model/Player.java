package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private String name;
	private List<Integer> hand = new ArrayList<Integer>();
	private List<Integer> buildings = new ArrayList<Integer>();
	private List<Integer> goods = new ArrayList<Integer>();
	
	public Player(String name) {
		super();
		this.name = name;
	}

	public int getVictory() {
		return 3;
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

}
