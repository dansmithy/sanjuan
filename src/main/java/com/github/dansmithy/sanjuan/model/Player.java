package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Transient;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;

public class Player {

	private String name;
	private List<Integer> hand = new ArrayList<Integer>();
	private List<Integer> buildings = new ArrayList<Integer>();
	private Map<Integer, Integer> goods = new HashMap<Integer, Integer>();
	private List<Integer> chapelCards = new ArrayList<Integer>();
	private Integer victoryPoints;
	@Transient
	private PlayerNumbers playerNumbers;
	
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
	
	public void addToHand(List<Integer> cards) {
		hand.addAll(cards);
	}

	public void removeHandCards(List<Integer> cardIds) {
		for (Integer cardId : cardIds) {
			removeHandCard(cardId);
		}
	}
	
	private void removeHandCard(Integer cardId) {
		hand.remove(cardId);
	}
	
	public void addGood(Integer factory, Integer good) {
		goods.put(factory, good);
	}	

	public String getName() {
		return name;
	}

	public List<Integer> getHand() {
		return hand;
	}

	public List<Integer> getBuildings() {
		return buildings;
	}

	public Map<Integer, Integer> getGoods() {
		return goods;
	}

	public List<Integer> getChapelCards() {
		return chapelCards;
	}

	public Integer getVictoryPoints() {
		return victoryPoints;
	}
	
	public void setVictoryPoints(Integer victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	public void setPlayerNumbers(PlayerNumbers privileges) {
		this.playerNumbers = privileges;
	}

	@JsonIgnore
	public PlayerNumbers getPlayerNumbers() {
		return playerNumbers;
	}



}
