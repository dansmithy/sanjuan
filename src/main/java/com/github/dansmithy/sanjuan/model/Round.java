package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;
import com.github.dansmithy.sanjuan.rest.jaxrs.GameViews;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Round {

	private String governor;
	private GovernorPhase governorPhase;
	private List<Phase> phases = new ArrayList<Phase>();
	private int playerCount;

	public Round(String governor, int playerCount) {
		this(governor, playerCount, GovernorPhase.EMPTY);
	}
	
	public Round(String governor, int playerCount, GovernorPhase governorPhase) {
		super();
		this.governor = governor;
		this.playerCount = playerCount;
		this.phases.add(new Phase(governor, playerCount));
		this.governorPhase = governorPhase;
	}	

	public Round() {
		super();
	}

	public String getGovernor() {
		return governor;
	}

	@JsonView(GameViews.Full.class)
	public List<Phase> getPhases() {
		return phases;
	}

	public int getPhaseNumber() {
		return phases.size();
	}
	
	@JsonProperty("governorPhaseFull")
	@JsonView(GameViews.Full.class)
	public GovernorPhase getGovernorPhase() {
		return governorPhase;
	}
	
	@JsonView(GameViews.PlayersOwn.class)
	@JsonProperty("governorPhase")
	public GovernorPhase getGovernorPhaseIfActive() {
		if (isGovernorPhase()) {
			return getGovernorPhase();
		} else {
			return null;
		}
	}

	@JsonView(GameViews.PlayersOwn.class)
	public Phase getCurrentPhase() {
		return phases.get(getPhaseNumber() - 1);
	}
	
	private boolean isGovernorPhase() {
		return getState().equals(RoundState.GOVERNOR);
	}
	
	public RoundState getState() {
		// calculate based on expected plays
		if (getCompletedPhaseCount() == getRequiredPhases()) {
			return RoundState.COMPLETED;
		}
		if (governorPhase != null && !governorPhase.isComplete()) {
			return RoundState.GOVERNOR;
		}
		return RoundState.PLAYING;
	}
	
	@JsonIgnore
	public boolean isComplete() {
		return getState().equals(RoundState.COMPLETED);
	}

	private int getRequiredPhases() {
		return playerCount == 2 ? 3 : playerCount;
	}

	private int getCompletedPhaseCount() {
		int count = 0;
		for (Phase phase : phases) {
			if (phase.getState().equals(PhaseState.COMPLETED)) {
				count++;
			}
		}
		return count;		
	}
	
	@JsonView(GameViews.PlayersOwn.class)
	public List<Role> getRemainingRoles() {
		List<Role> remainingRoles = new ArrayList<Role>(Arrays.asList(Role.values()));
		remainingRoles.removeAll(getPlayedRoles());
		remainingRoles.remove(getCurrentRole());
		return remainingRoles;
	}
	
	@JsonView(GameViews.PlayersOwn.class)
	public Role getCurrentRole() {
		if (getState().equals(RoundState.GOVERNOR)) {
			return Role.GOVERNOR;
		}
		if (getState().equals(RoundState.PLAYING)) {
			return getCurrentPhase().getRole();
		}
		return null;
	}

	@JsonView(GameViews.PlayersOwn.class)
	public List<Role> getPlayedRoles() {
		List<Role> playedRoles = new ArrayList<Role>();
		if (!isGovernorPhase()) {
			playedRoles.add(Role.GOVERNOR);
		}
		for (Phase phase : phases) {
			if (phase.isComplete()) {
				playedRoles.add(phase.getRole());
			}
		}
		return playedRoles;
		
	}

	public Phase nextPhase(PlayerCycle cycle) {
		String nextLeadPlayer = cycle.next(getCurrentPhase().getLeadPlayer());
		Phase nextPhase = new Phase(nextLeadPlayer, playerCount);
		phases.add(nextPhase);
		return nextPhase;
	}

}
