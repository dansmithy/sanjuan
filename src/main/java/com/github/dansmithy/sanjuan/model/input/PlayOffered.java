package com.github.dansmithy.sanjuan.model.input;

import java.util.List;

public class PlayOffered {

	private List<Integer> prospected;
	private List<Integer> councilOffered;
	private int councilRetainCount;
	private boolean councilCanDiscardHandCards;
	
	public List<Integer> getCouncilOffered() {
		return councilOffered;
	}
	public void setCouncilOffered(List<Integer> councilOptions) {
		this.councilOffered = councilOptions;
	}
	public List<Integer> getProspected() {
		return prospected;
	}
	public void setProspected(List<Integer> prospected) {
		this.prospected = prospected;
	}
	public int getCouncilRetainCount() {
		return councilRetainCount;
	}
	public void setCouncilRetainCount(int councilRetainCount) {
		this.councilRetainCount = councilRetainCount;
	}
	public boolean isCouncilCanDiscardHandCards() {
		return councilCanDiscardHandCards;
	}
	public void setCouncilCanDiscardHandCards(boolean councilCanDiscardHandCards) {
		this.councilCanDiscardHandCards = councilCanDiscardHandCards;
	}

}
