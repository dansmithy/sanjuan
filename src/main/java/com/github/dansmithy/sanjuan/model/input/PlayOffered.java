package com.github.dansmithy.sanjuan.model.input;

import java.util.List;

public class PlayOffered {

	private List<Integer> prospected;
	private List<Integer> councilOptions;
	
	public List<Integer> getCouncilOptions() {
		return councilOptions;
	}
	public void setCouncilOptions(List<Integer> councilOptions) {
		this.councilOptions = councilOptions;
	}
	public List<Integer> getProspected() {
		return prospected;
	}
	public void setProspected(List<Integer> prospected) {
		this.prospected = prospected;
	}

}
