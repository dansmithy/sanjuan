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

	public PlayCoords nextPlay() {
		return new PlayCoords(gameId, getRoundNumber(), getPhaseNumber(), getPlayNumber() + 1);
	}

	public PlayCoords nextPhase() {
		return new PlayCoords(gameId, getRoundNumber(), getPhaseNumber() + 1, 0);
	}

	public PlayCoords nextRound() {
		return new PlayCoords(gameId, getRoundNumber() + 1, 1, 0);
	}
	
	public String getPlayLocation() {
		return String.format("rounds.%d.phases.%d.plays.%d", getRoundIndex(), getPhaseIndex(), getPlayIndex());
	}
	
	public String getPhaseLocation() {
		return String.format("rounds.%d.phases.%d", getRoundIndex(), getPhaseIndex());
	}

	public String getRoundLocation() {
		return String.format("rounds.%d", getRoundIndex());
	}
}
