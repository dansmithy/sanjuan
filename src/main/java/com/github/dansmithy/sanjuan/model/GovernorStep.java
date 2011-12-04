package com.github.dansmithy.sanjuan.model;

import java.util.List;

public class GovernorStep {

	private PlayState state = PlayState.AWAITING_INPUT;
	private String playerName;
	private int numberOfCardsToDiscard;
	private List<Integer> cardsToDiscard;
	private boolean chapelOwner;
	
	public GovernorStep() {
		super();
	}

	public GovernorStep(String playerName, int numberOfCardsToDiscard,
			boolean chapelOwner) {
		super();
		this.playerName = playerName;
		this.numberOfCardsToDiscard = numberOfCardsToDiscard;
		this.chapelOwner = chapelOwner;
	}

	public PlayState getState() {
		return state;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getNumberOfCardsToDiscard() {
		return numberOfCardsToDiscard;
	}

	public boolean isChapelOwner() {
		return chapelOwner;
	}
	
	public boolean isComplete() {
		return state.equals(PlayState.COMPLETED);
	}

	public void setState(PlayState state) {
		this.state = state;
	}

	public List<Integer> getCardsToDiscard() {
		return cardsToDiscard;
	}

	public void setCardsToDiscard(List<Integer> cardsToDiscard) {
		this.cardsToDiscard = cardsToDiscard;
	}
	
}
