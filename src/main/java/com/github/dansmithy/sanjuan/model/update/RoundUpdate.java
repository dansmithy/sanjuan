package com.github.dansmithy.sanjuan.model.update;

import com.github.dansmithy.sanjuan.model.Round;

public class RoundUpdate extends GameUpdate {

	private Integer roundIndex;
	private Round round;
	
	public RoundUpdate(Integer roundIndex, Round round) {
		super();
		this.roundIndex = roundIndex;
		this.round = round;
	}
	
	protected RoundUpdate(Integer roundIndex) {
		super();
		this.roundIndex = roundIndex;
	}

	public Integer getRoundIndex() {
		return roundIndex;
	}
	public void setRoundIndex(Integer roundIndex) {
		this.roundIndex = roundIndex;
	}
	public Round getRound() {
		return round;
	}
	public void setRound(Round round) {
		this.round = round;
	}
	
}
