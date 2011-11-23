package com.github.dansmithy.sanjuan.model.update;

import java.util.Iterator;
import java.util.List;

public class PlayerCycle {

	private List<String> playerNames;

	public PlayerCycle(List<String> playerNames) {
		super();
		this.playerNames = playerNames;
	}

	public String next(String currentPlayer) {
		int index = playerNames.indexOf(currentPlayer);
		index++;
		if (index >= playerNames.size()) {
			index = 0;
		}
		return playerNames.get(index);
	}
	
	public Iterable<String> startAt(String startingPlayer) {
		return new IterableCycle(startingPlayer);
	}
	
	private class IterableCycle implements Iterable<String> {

		private String startingPlayer;
		
		public IterableCycle(String startingPlayer) {
			super();
			this.startingPlayer = startingPlayer;
		}

		@Override
		public Iterator<String> iterator() {
			return new PlayerCycleIterator(startingPlayer);
		}
		
	}
	private class PlayerCycleIterator implements Iterator<String> {

		private String currentPlayer;
		private int currentPlayerNumber = 0;
		
		public PlayerCycleIterator(String startingPlayer) {
			this.currentPlayer = startingPlayer;
		}

		@Override
		public boolean hasNext() {
			return currentPlayerNumber < playerNames.size();
		}

		@Override
		public String next() {
			String toReturn = currentPlayer;
			currentPlayer = PlayerCycle.this.next(currentPlayer);
			currentPlayerNumber++;
			return toReturn;
		}

		@Override
		public void remove() {
			throw new IllegalStateException("Cannot remove players");
		}
		
	}

	
}
