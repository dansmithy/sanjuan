package com.github.dansmithy.sanjuan.model;

import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;

public class Play {

	private String player;
	private boolean hasPrivilige;
	private PlayState state = PlayState.AWAITING_INPUT;

	private PlayChoice playChoice;
	private PlayOffered offered;

	public Play() {
		super();
	}

	public Play(String player, boolean hasPrivilige) {
		super();
		this.player = player;
		this.hasPrivilige = hasPrivilige;
	}

	public String getPlayer() {
		return player;
	}

	public boolean isHasPrivilige() {
		return hasPrivilige;
	}

	public PlayState getState() {
		return state;
	}

	public PlayChoice getPlayChoice() {
		return playChoice;
	}

	public void setPlayChoice(PlayChoice playChoice) {
		this.playChoice = playChoice;
	}
	
	public PlayOffered getOffered() {
		return offered;
	}

	public void setOffered(PlayOffered offered) {
		this.offered = offered;
	}

	public void makePlay(PlayChoice playChoice) {
		this.playChoice = playChoice;
		state = PlayState.COMPLETED;
	}

	public boolean isComplete() {
		return PlayState.COMPLETED.equals(state);
	}

}
