package com.github.dansmithy.sanjuan.game;

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
	
	public void addCouncillorRetainCard() {
		this.councillorRetainCards++;
	}	
	
}
