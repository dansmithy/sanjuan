package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class GovernorPhase {

	private List<GovernorStep> governorSteps = new ArrayList<GovernorStep>();
	
	public static GovernorPhase EMPTY = new GovernorPhase(); 

	public GovernorPhase(List<GovernorStep> governorSteps) {
		super();
		this.governorSteps = governorSteps;
	}

	public GovernorPhase() {
		super();
	}
	
	public PlayState getState() {
		for (GovernorStep step : governorSteps) {
			if (step.getState().equals(PlayState.AWAITING_INPUT)) {
				return PlayState.AWAITING_INPUT;
			}
		}
		return PlayState.COMPLETED;
	}
	
	public GovernorStep getCurrentStep() {
		for (GovernorStep step : governorSteps) {
			if (step.isComplete()) {
				return step;
			}
		}
		return null;
	}
	
	public List<GovernorStep> getGovernorSteps() {
		return governorSteps;
	}

	public boolean isComplete() {
		return getState().equals(PlayState.COMPLETED);
	}
}
