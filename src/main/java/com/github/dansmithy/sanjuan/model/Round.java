package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

public class Round {

	private String governor;
	private List<Phase> phases = new ArrayList<Phase>();
	
	public Round(String governor) {
		super();
		this.governor = governor;
	}

	public String getGovernor() {
		return governor;
	}

	public List<Phase> getPhases() {
		return phases;
	}

}
