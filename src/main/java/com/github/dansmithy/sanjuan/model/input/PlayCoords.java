package com.github.dansmithy.sanjuan.model.input;

public class PlayCoords {

	private Long gameId;
	private int roundNumber;
	private int phaseNumber;
	private int playNumber;

	public PlayCoords(Long gameId, int roundNumber, int phaseNumber,
			int playNumber) {
		super();
		this.gameId = gameId;
		this.roundNumber = roundNumber;
		this.phaseNumber = phaseNumber;
		this.playNumber = playNumber;
	}

	public Long getGameId() {
		return gameId;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public int getPhaseNumber() {
		return phaseNumber;
	}

	public int getPlayNumber() {
		return playNumber;
	}

	public int getRoundIndex() {
		return roundNumber - 1;
	}
	
	public int getPhaseIndex() {
		return phaseNumber - 1;
	}
	
	public int getPlayIndex() {
		return playNumber - 1;
	}


}
