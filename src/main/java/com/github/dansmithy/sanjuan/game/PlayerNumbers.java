package com.github.dansmithy.sanjuan.game;

public class PlayerNumbers extends PlayerPrivileges {

	private static final int goodsCanTrade = 1;
	private static final int goodsCanProduce = 1;
	private static final int builderDiscountOnProduction = 0;
	private static final int builderDiscountOnViolet = 0;
	private static final int cardsCanHold = 7;
	private static final int prospectedCards = 0;
	private static final int councillorOfferedCards = 2;
	private static final int councillorRetainCards = 1;
	
	private PlayerPrivileges privileges = new PlayerPrivileges();
	
	public PlayerNumbers() {
		super(goodsCanTrade, goodsCanProduce, builderDiscountOnProduction, builderDiscountOnViolet, prospectedCards, councillorOfferedCards, councillorRetainCards);
	}
	
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

	public static int getCardscanhold() {
		return cardsCanHold;
	}	
	
	
}
