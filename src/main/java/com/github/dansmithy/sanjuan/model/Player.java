package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Transient;

import com.github.dansmithy.sanjuan.game.GameService;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;

public class Player {

	private String name;
	private List<Integer> hand = new ArrayList<Integer>();
	private List<Integer> buildings = new ArrayList<Integer>();
	private List<Integer> goods = new ArrayList<Integer>();
	private List<Integer> chapelCards = new ArrayList<Integer>();

	@Transient
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
	
	public List<Integer> getChapelCards() {
		return chapelCards;
	}

	public void setChapelCards(List<Integer> chapelCards) {
		this.chapelCards = chapelCards;
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
