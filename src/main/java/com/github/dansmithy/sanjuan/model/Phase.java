package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

public class Phase {

	private PhaseType type;
	private List<Play> plays = new ArrayList<Play>();
	private String leadPlayer;
	private PhaseState state = PhaseState.AWAITING_ROLE_CHOICE;
	
	public PhaseType getType() {
		return type;
	}
	public List<Play> getPlays() {
		return plays;
	}
	public String getLeadPlayer() {
		return leadPlayer;
	}
	public PhaseState getState() {
		return state;
	}
	public void selectRole(String role) {
		this.type = PhaseType.valueOf(role.toUpperCase());
		this.state = PhaseState.PLAYING;
		plays.add(new Play(this.leadPlayer, true));
	}
}
