package com.github.dansmithy.sanjuan.model.update;

import com.github.dansmithy.sanjuan.model.Play;

public class PlayChoice extends PhaseUpdate {

	private Integer playIndex;
	private Play play;
	
	public PlayChoice(Integer roundId, Integer phaseId, Integer playIndex, Play play) {
		super(roundId, phaseId);
		this.playIndex = playIndex;
		this.play = play;
	}
	public Integer getPlayIndex() {
		return playIndex;
	}
	public void setPlayIndex(Integer playIndex) {
		this.playIndex = playIndex;
	}
	public Play getPlay() {
		return play;
	}
	public void setPlay(Play play) {
		this.play = play;
	}
	
	
}
