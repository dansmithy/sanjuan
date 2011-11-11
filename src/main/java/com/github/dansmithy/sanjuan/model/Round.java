package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.github.dansmithy.sanjuan.model.update.PlayerCycle;

public class Round {

	private String governor;
	private List<Phase> phases = new ArrayList<Phase>();
	private int playerCount;

	public Round(String governor, int playerCount) {
		super();
		this.governor = governor;
		this.playerCount = playerCount;
		this.phases.add(new Phase(governor, playerCount));
	}

	public Round() {
		super();
	}

	public String getGovernor() {
		return governor;
	}

	public List<Phase> getPhases() {
		return phases;
	}

	public int getPhaseNumber() {
		return phases.size();
	}

	@JsonIgnore
	public Phase getCurrentPhase() {
		return phases.get(getPhaseNumber() - 1);
	}
	
	public RoundState getState() {
		// calculate based on expected plays
		if (getCompletedPhaseCount() == getRequiredPhases()) {
			return RoundState.COMPLETED;
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
	
	@JsonIgnore
	public List<Role> getRemainingRoles() {
		List<Role> remainingRoles = new ArrayList<Role>(Arrays.asList(Role.values()));
		remainingRoles.remove(Role.GOVERNOR);
		remainingRoles.removeAll(getPlayedRoles());
		return remainingRoles;
	}

	private List<Role> getPlayedRoles() {
		List<Role> playedRoles = new ArrayList<Role>();
		for (Phase phase : phases) {
			playedRoles.add(phase.getRole());
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
