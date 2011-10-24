package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

	private List<Integer> supply = new ArrayList<Integer>();
	private List<Integer> discard = new ArrayList<Integer>();
	
	public Deck(List<Integer> supply) {
		super();
		this.supply = supply;
	}
	
	public void discard(List<Integer> cards) {
		discard.addAll(cards);
	}	
	
	public void discard(Integer cardId) {
		discard.add(cardId);
	}
	
	public int getSupplyCount() {
		return supply.size();
	}
	
	public int getDiscardCount() {
		return discard.size();
	}

	public Integer takeOne() {
		if (getSupplyCount() == 0) {
			reshuffleDeck();
		}
		Integer cardId = supply.remove(0);
		return cardId;
	}
	
	public List<Integer> take(int count) {
		List<Integer> cards = new ArrayList<Integer>();
		for (int num = 0; num < count; num++) {
			cards.add(takeOne());
		}
		return cards;
	}
	
	public void reshuffleDeck() {
		supply.addAll(discard);
		discard.clear();
		Collections.shuffle(supply);
	}	

}
