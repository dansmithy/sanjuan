package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.github.dansmithy.sanjuan.model.update.GameUpdate;


public class Round {

	private String governor;
	private List<Phase> phases = new ArrayList<Phase>();
	private RoundState state = RoundState.PLAYING;
	
	public Round(String governor) {
		super();
		this.governor = governor;
		this.phases.add(new Phase(governor));
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

	public GameUpdate moveToNext() {
		Phase phase = getCurrentPhase();
		return null;
//		phase.moveToNext();
	}

	@JsonIgnore
	private Phase getCurrentPhase() {
		return phases.get(getPhaseNumber()-1);
	}

	public boolean isComplete() {
		return state.equals(RoundState.COMPLETED);
	}

}
