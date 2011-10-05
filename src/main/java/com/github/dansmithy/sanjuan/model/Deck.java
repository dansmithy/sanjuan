package com.github.dansmithy.sanjuan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Deck {

	private List<Integer> supply = new ArrayList<Integer>();
	private List<Integer> discard = new ArrayList<Integer>();
	
	public Deck(List<Integer> supply) {
		super();
		this.supply = supply;
	}
	
	@JsonIgnore
	public List<Integer> getSupply() {
		return supply;
	}

	@JsonIgnore
	public List<Integer> getDiscard() {
		return discard;
	}
	
	public void discard(Integer[] payment) {
		discard.addAll(Arrays.asList(payment));
	}	
	
	public int getSupplyCount() {
		return supply.size();
	}
	
	public int getDiscardCount() {
		return discard.size();
	}

	public Integer takeOne() {
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

}
