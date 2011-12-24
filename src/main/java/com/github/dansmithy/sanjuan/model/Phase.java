package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameViews;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Phase {

	private Role role;
	private List<Play> plays = new ArrayList<Play>();
	private String leadPlayer;
	private int playerCount;
	private Tariff tariff;
	
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
	
	@JsonView(GameViews.Full.class)
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
	
	private int getRequiredPlays() {
		return playerCount;
	}

	public int getPlayNumber() {
		return plays.size();
	}
	
	@JsonView(GameViews.PlayersOwn.class)
	public Play getCurrentPlay() {
		return plays.isEmpty() ? null : plays.get(getPlayNumber() - 1);
	}
	
	public void selectRole(Role role) {
		this.role = role;
	}
	
	public Play nextPlay(PlayerCycle playerCycle) {
		
		String nextPlayer = leadPlayer;
		if (getCurrentPlay() != null) {
			nextPlayer = playerCycle.next(getCurrentPlay().getPlayer());
		}
		Play nextPlay = new Play(nextPlayer, nextPlayer.equals(leadPlayer)); 
		plays.add(nextPlay);
		return nextPlay;
	}
}
