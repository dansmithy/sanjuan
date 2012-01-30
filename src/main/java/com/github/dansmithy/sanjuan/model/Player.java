package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.data.annotation.Transient;

import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameViews;
import com.github.dansmithy.sanjuan.security.user.AuthenticatedUser;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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
	
	public void removeHandCard(Integer cardId) {
		hand.remove(cardId);
	}
	
	public void addGood(Integer factory, Integer good) {
		goods.put(factory, good);
	}	

	public void addChapelCard(Integer chapelCard) {
		chapelCards.add(chapelCard);
	}

	public String getName() {
		return name;
	}

	/**
	 * Used for Java-code access to hand cards for any player
	 */
	@JsonView(GameViews.Full.class)
	public List<Integer> getHandCards() {
		return hand;
	}
	
	/**
	 * Used to produce JSON for current player only
	 */
	public List<Integer> getHand() {
		if (isAuthenticatedPlayer()) {
			return hand;
		} else {
			return null;
		}
	}	
	
	/**
	 * For JSON only
	 */
	public int getHandCount() {
		return getHandCards().size();
	}

	public List<Integer> getBuildings() {
		return buildings;
	}

	@JsonView(GameViews.Full.class)
	public Map<Integer, Integer> getGoods() {
		return goods;
	}
	
	/**
	 * For JSON only
	 */
	public Set<Integer> getProducedFactories() {
		return getGoods().keySet();
	}

	@JsonView(GameViews.Full.class)
	public List<Integer> getChapelCards() {
		return chapelCards;
	}
	
	/**
	 * For JSON only
	 */
	public int getChapelCardCount() {
		return getChapelCards().size();
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
	
	private boolean isAuthenticatedPlayer() {
		return name.equals(AuthenticatedUser.get());
	}

}
