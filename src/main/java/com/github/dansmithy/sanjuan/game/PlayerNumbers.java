package com.github.dansmithy.sanjuan.game;

import java.util.ArrayList;
import java.util.List;

public class PlayerNumbers {

	private int goodsCanTrade = 1;
	private int goodsCanProduce = 1;
	private int builderDiscountOnProduction = 0;
	private int builderDiscountOnViolet = 0;
	private int prospectedCards = 0;
	private int councillorOfferedCards = 2;
	private int councillorRetainCards = 1;
	
	private int cardsCanHold = 7;
	private boolean councillorCanDiscardHandCards = false;
	private List<BonusPair> traderBonusCounts = new ArrayList<BonusPair>();
	private List<BonusPair> producerBonusCounts = new ArrayList<BonusPair>();
	private int builderBonusOnViolet = 0;
	
	private PlayerPrivileges privileges = new PlayerPrivileges();
	
	public PlayerPrivileges getPrivileges() {
		return privileges;
	}
	
	public int getTotalGoodsCanTrade(boolean withPrivilege) {
		return withPrivilege ? privileges.getGoodsCanTrade() + goodsCanTrade : goodsCanTrade; 
	}
	public int getTotalGoodsCanProduce(boolean withPrivilege) {
		return withPrivilege ? privileges.getGoodsCanProduce() + goodsCanProduce : goodsCanProduce; 
	}
	public int getTotalBuilderDiscountOnProduction(boolean withPrivilege) {
		return withPrivilege ? privileges.getBuilderDiscountOnProduction() + builderDiscountOnProduction : builderDiscountOnProduction; 
	}
	public int getTotalBuilderDiscountOnViolet(boolean withPrivilege) {
		return withPrivilege ? privileges.getBuilderDiscountOnViolet() + builderDiscountOnViolet : builderDiscountOnViolet; 
	}
	public int getTotalProspectedCards(boolean withPrivilege) {
		return withPrivilege ? privileges.getProspectedCards() + prospectedCards : prospectedCards; 
	}
	public int getTotalCouncillorOfferedCards(boolean withPrivilege) {
		return withPrivilege ? privileges.getCouncillorOfferedCards() + councillorOfferedCards : councillorOfferedCards; 
	}
	public int getTotalCouncillorRetainCards(boolean withPrivilege) {
		return withPrivilege ? privileges.getCouncillorRetainCards() + councillorRetainCards : councillorRetainCards; 
	}
	
	public boolean isCouncillorCanDiscardHandCards() {
		return councillorCanDiscardHandCards;
	}

	public void councillorCanDiscardHandCards() {
		this.councillorCanDiscardHandCards = true;
	}

	public int getCardsCanHold() {
		return cardsCanHold;
	}	
	
	public int getBuilderBonusOnViolet() {
		return builderBonusOnViolet;
	}

	public void addCouncillorRetainCard() {
		this.councillorRetainCards++;
	}	
	public void addBuilderDiscountOnProduction() {
		this.builderDiscountOnProduction++;
	}	
	public void addBuilderDiscountOnViolet() {
		this.builderDiscountOnViolet++;
	}
	public void addBuilderBonusOnViolet() {
		this.builderBonusOnViolet++;
	}	
	public void addGoodCanTrade() {
		goodsCanTrade++;
	}	
	public void addGoodCanProduce() {
		goodsCanProduce++;
	}
	public void addCardsCanHold(int number) {
		cardsCanHold += number;
	}	
	
	public void registerTraderBonusCard(int minValue, int bonusCards) {
		traderBonusCounts.add(new BonusPair(minValue, bonusCards));
	}
	
	public void registerProducerBonusCard(int minValue, int bonusCards) {
		producerBonusCounts.add(new BonusPair(minValue, bonusCards));
	}	

	public int getTraderBonusCards(int value) {
		return calculateBonusCards(traderBonusCounts, value);
	}
	
	public int getProducerBonusCards(int value) {
		return calculateBonusCards(producerBonusCounts, value);
	}
	
	private int calculateBonusCards(List<BonusPair> pairs,
			int value) {
		int bonusCards = 0;
		for (BonusPair pair : pairs) {
			if (value >= pair.getMinValue()) {
				bonusCards += pair.getBonusCards();
			}
		}
		return bonusCards;
	}

	private static class BonusPair {
		private int minValue;
		private int bonusCards;
		public BonusPair(int minValue, int bonusCards) {
			super();
			this.minValue = minValue;
			this.bonusCards = bonusCards;
		}
		public int getMinValue() {
			return minValue;
		}
		public int getBonusCards() {
			return bonusCards;
		}
		
		
	}

	
}
