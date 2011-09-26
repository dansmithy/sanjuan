package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.List;

public class Phase {

	private PhaseType type;
	private List<Play> plays = new ArrayList<Play>();
	private String leadPlayer;
	public PhaseType getType() {
		return type;
	}
	public List<Play> getPlays() {
		return plays;
	}
	public String getLeadPlayer() {
		return leadPlayer;
	}
	
}
