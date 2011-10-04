package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;

public class Phase {

	private PhaseType type;
	private List<Play> plays = new ArrayList<Play>();
	private String leadPlayer;
	private int playerCount;
	
	public Phase() {
		super();
	}
	
	public Phase(String leadPlayer, int playerCount) {
		super();
		this.leadPlayer = leadPlayer;
		this.playerCount = playerCount;
	}

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
		// calculate based on expected plays
		if (plays.isEmpty()) {
			return PhaseState.AWAITING_ROLE_CHOICE;
		}
		if (getCompletedPlayCount() == getRequiredPlays()) {
			return PhaseState.COMPLETED;
		}
		return PhaseState.PLAYING;
	}
	
	private int getCompletedPlayCount() {
		int count = 0;
		for (Play play : plays) {
			if (play.getState().equals(PlayState.COMPLETED)) {
				count++;
			}
		}
		return count;
	}

	@JsonIgnore
	public boolean isComplete() {
		return getState().equals(PhaseState.COMPLETED);
	}
	
	@JsonIgnore
	public int getRequiredPlays() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getPlayNumber() {
		return plays.size();
	}
	
	private Play getCurrentPlay() {
		return plays.get(getPlayNumber() - 1);
	}
	
	public void selectRole(String role) {
		this.type = PhaseType.valueOf(role.toUpperCase());
		plays.add(new Play(this.leadPlayer, true));
	}
	
	public Play nextPlay(PlayerCycle playerCycle) {
		String nextPlayer = playerCycle.next(getCurrentPlay().getPlayer());
		Play nextPlay = new Play(nextPlayer, nextPlayer.equals(leadPlayer)); 
		plays.add(nextPlay);
		return nextPlay;
	}	


}
