package com.github.dansmithy.sanjuan.rest.beans;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.rest.jaxrs.CardResource;

@Named
public class CardBean implements CardResource {

	private CardFactory cardFactory;
	
	@Inject
	public CardBean(CardFactory cardFactory) {
		super();
		this.cardFactory = cardFactory;
	}

	@Override
	public Map<Integer, String> getCards() {
		return cardFactory.getCardMap();
	}

	@Override
	public Map<String, BuildingType> getCardTypes() {
		return cardFactory.getCardTypes();
	}

}
