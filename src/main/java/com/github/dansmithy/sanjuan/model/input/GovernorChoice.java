package com.github.dansmithy.sanjuan.model.input;

import java.util.ArrayList;
import java.util.List;

public class GovernorChoice {

	private List<Integer> cardsToDiscard = new ArrayList<Integer>();
	private Integer chapelCard;

	public List<Integer> getCardsToDiscard() {
		return cardsToDiscard;
	}

	public void setCardsToDiscard(List<Integer> cardsToDiscard) {
		this.cardsToDiscard = cardsToDiscard;
	}

	public void setChapelCard(Integer chapelCard) {
		this.chapelCard = chapelCard;
	}

	public Integer getChapelCard() {
		return chapelCard;
	}
	
}
