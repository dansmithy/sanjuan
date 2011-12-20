package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;

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

	@JsonIgnore
	public List<Phase> getPhases() {
		return phases;
	}

	public int getPhaseNumber() {
		return phases.size();
	}
	
	@JsonIgnore
	public GovernorPhase getGovernorPhaseHidden() {
		return governorPhase;
	}
	
	public GovernorPhase getGovernorPhase() {
		if (isGovernorPhase()) {
			return getGovernorPhaseHidden();
		} else {
			return null;
		}
	}
	

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
	
	public List<Role> getRemainingRoles() {
		List<Role> remainingRoles = new ArrayList<Role>(Arrays.asList(Role.values()));
		remainingRoles.removeAll(getPlayedRoles());
		return remainingRoles;
	}

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
