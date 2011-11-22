package com.github.dansmithy.sanjuan.model;

public class GovernorStep {

	private PlayState state = PlayState.AWAITING_INPUT;
	private String playerName;
	private int cardsToDiscard;
	private boolean chapelOwner;
	
	public GovernorStep() {
		super();
	}

	public GovernorStep(String playerName, int cardsToDiscard,
			boolean chapelOwner) {
		super();
		this.playerName = playerName;
		this.cardsToDiscard = cardsToDiscard;
		this.chapelOwner = chapelOwner;
	}

	public PlayState getState() {
		return state;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getCardsToDiscard() {
		return cardsToDiscard;
	}

	public boolean isChapelOwner() {
		return chapelOwner;
	}
	
	public boolean isComplete() {
		return state.equals(PlayState.COMPLETED);
	}
	
}
