package com.github.dansmithy.sanjuan.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.game.aspect.AuthenticatedUser;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.input.PlayOffered;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameViews;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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

	@JsonView(GameViews.Full.class)
	public PlayChoice getPlayChoice() {
		return playChoice;
	}

	
	public PlayOffered getOffered() {
		return isAuthenticatedPlayer() ? offered : null;
	}

	public PlayOffered createOffered() {
		this.offered = new PlayOffered();
		return this.offered;
	}

	public void completePlay(PlayChoice playChoice) {
		this.playChoice = playChoice;
		state = PlayState.COMPLETED;
	}

	public boolean isComplete() {
		return PlayState.COMPLETED.equals(state);
	}

	private boolean isAuthenticatedPlayer() {
		return getPlayer().equals(AuthenticatedUser.get());
	}
}
