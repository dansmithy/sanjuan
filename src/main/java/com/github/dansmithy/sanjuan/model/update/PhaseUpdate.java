package com.github.dansmithy.sanjuan.model.update;

import com.github.dansmithy.sanjuan.model.Phase;

public class PhaseUpdate extends RoundUpdate {

	private Integer phaseIndex;
	private Phase phase;
	
	public PhaseUpdate(Integer roundIndex, Integer phaseIndex, Phase phase) {
		super(roundIndex);
		this.phaseIndex = phaseIndex;
		this.phase = phase;
	}
	
	protected PhaseUpdate(Integer roundIndex, Integer phaseIndex) {
		super(roundIndex);
		this.phaseIndex = phaseIndex;
	}

	public Integer getPhaseIndex() {
		return phaseIndex;
	}
	public void setPhaseIndex(Integer phaseIndex) {
		this.phaseIndex = phaseIndex;
	}
	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	
}
