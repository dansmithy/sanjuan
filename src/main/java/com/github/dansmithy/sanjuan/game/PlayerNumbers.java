package com.github.dansmithy.sanjuan.game;

import java.util.ArrayList;
import java.util.List;

import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;

public class PlayerNumbers implements BonusCardMatcher {

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
	private boolean libraryOwner = false;
	private boolean chapelOwner = false;
	private boolean useLibrary = true; 
	
	private List<BonusCardMatcher> matchers = new ArrayList<BonusCardMatcher>();
	
	private PlayerPrivileges privileges = new PlayerPrivileges();
	
	public int getTotalGoodsCanTrade(boolean withPrivilege) {
		return withPrivilege ? (privileges.getGoodsCanTrade()*getTimesCanUsePrivilege()) + goodsCanTrade : goodsCanTrade; 
	}
	public int getTotalGoodsCanProduce(boolean withPrivilege) {
		return withPrivilege ? (privileges.getGoodsCanProduce()*getTimesCanUsePrivilege()) + goodsCanProduce : goodsCanProduce; 
	}
	public int getTotalBuilderDiscountOnProduction(boolean withPrivilege) {
		return withPrivilege ? (privileges.getBuilderDiscountOnProduction()*getTimesCanUsePrivilege()) + builderDiscountOnProduction : builderDiscountOnProduction; 
	}
	public int getTotalBuilderDiscountOnViolet(boolean withPrivilege) {
		return withPrivilege ? (privileges.getBuilderDiscountOnViolet()*getTimesCanUsePrivilege()) + builderDiscountOnViolet : builderDiscountOnViolet; 
	}
	public int getTotalProspectedCards(boolean withPrivilege) {
		return withPrivilege ? (privileges.getProspectedCards()*getTimesCanUsePrivilege()) + prospectedCards : prospectedCards; 
	}
	public int getTotalCouncillorOfferedCards(boolean withPrivilege) {
		return withPrivilege ? (privileges.getCouncillorOfferedCards()*getTimesCanUsePrivilege()) + councillorOfferedCards : councillorOfferedCards; 
	}
	private int getTimesCanUsePrivilege() {
		return libraryOwner && useLibrary ? 2 : 1;
	}
	
	public boolean isCouncillorCanDiscardHandCards() {
		return councillorCanDiscardHandCards;
	}

	public void councillorCanDiscardHandCards() {
		this.councillorCanDiscardHandCards = true;
	}
	
	public int getCouncillorRetainCards() {
		return councillorRetainCards;
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
	
	public void addBonusCardMatcher(BonusCardMatcher matcher) {
		matchers.add(matcher);
	}
	
	public void setUseLibrary() {
		useLibrary = true;
	}
	public void setLibraryOwner() {
		libraryOwner = true;
	}
	
	public void setChapelOwner() {
		chapelOwner = true;
	}
	
	public boolean isChapelOwner() {
		return chapelOwner;
	}
	
	public int getTraderBonusCards(int value) {
		return calculateBonusCards(traderBonusCounts, value);
	}
	
	public int getProducerBonusCards(int value) {
		return calculateBonusCards(producerBonusCounts, value);
	}
	
	@Override
	public int getBonusCardMatches(Player player, Role role) {
		int bonusCards = 0;
		for (BonusCardMatcher matcher : matchers) {
			bonusCards += matcher.getBonusCardMatches(player, role);
		}
		return bonusCards;
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
