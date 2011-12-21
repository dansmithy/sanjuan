package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Transient;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Phase {

	private Role role;
	private List<Play> plays = new ArrayList<Play>();
	private String leadPlayer;
	private int playerCount;
	private Tariff tariff;
	
	@Transient
	private boolean authenticatedPlayer = false;
	
	public Phase() {
		super();
	}
	
	public Phase(String leadPlayer, int playerCount) {
		super();
		this.leadPlayer = leadPlayer;
		this.playerCount = playerCount;
	}

	public Role getRole() {
		return role;
	}
	
	@JsonIgnore
	public List<Play> getPlays() {
		return plays;
	}
	
	public String getLeadPlayer() {
		return leadPlayer;
	}
	
	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
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

	public int getPlayNumber() {
		return plays.size();
	}
	
	@JsonIgnore
	public Play getCurrentPlayHidden() {
		return plays.isEmpty() ? null : plays.get(getPlayNumber() - 1);
	}
	
	public Play getCurrentPlay() {
		return isAuthenticatedPlayer() ? getCurrentPlayHidden() : null;
	}

	public void selectRole(Role role) {
		this.role = role;
	}
	
	public Play nextPlay(PlayerCycle playerCycle) {
		
		String nextPlayer = leadPlayer;
		if (getCurrentPlayHidden() != null) {
			nextPlayer = playerCycle.next(getCurrentPlayHidden().getPlayer());
		}
		Play nextPlay = new Play(nextPlayer, nextPlayer.equals(leadPlayer)); 
		plays.add(nextPlay);
		return nextPlay;
	}
	
	@JsonIgnore
	public boolean isAuthenticatedPlayer() {
		return authenticatedPlayer;
	}

	public void setAuthenticatedPlayer(boolean currentPlayer) {
		this.authenticatedPlayer = currentPlayer;
	}	


}
