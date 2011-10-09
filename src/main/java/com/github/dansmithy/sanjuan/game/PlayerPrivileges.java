package com.github.dansmithy.sanjuan.game;

public class PlayerPrivileges {

	private int goodsCanTrade = 1;
	private int goodsCanProduce = 1;
	private int builderDiscountOnProduction = 1;
	private int builderDiscountOnViolet = 1;
	private int prospectedCards = 1;
	private int councillorOfferedCards = 3;
	private int councillorRetainCards = 0;
	
	public PlayerPrivileges() {
		super();
	}
	
	public PlayerPrivileges(int goodsCanTrade, int goodsCanProduce,
			int builderDiscountOnProduction, int builderDiscountOnViolet,
			int prospectedCards, int councillorOfferedCards,
			int councillorRetainCards) {
		super();
		this.goodsCanTrade = goodsCanTrade;
		this.goodsCanProduce = goodsCanProduce;
		this.builderDiscountOnProduction = builderDiscountOnProduction;
		this.builderDiscountOnViolet = builderDiscountOnViolet;
		this.prospectedCards = prospectedCards;
		this.councillorOfferedCards = councillorOfferedCards;
		this.councillorRetainCards = councillorRetainCards;
	}
	
	public int getGoodsCanTrade() {
		return goodsCanTrade;
	}
	public int getGoodsCanProduce() {
		return goodsCanProduce;
	}
	public int getBuilderDiscountOnProduction() {
		return builderDiscountOnProduction;
	}
	public int getBuilderDiscountOnViolet() {
		return builderDiscountOnViolet;
	}
	public int getProspectedCards() {
		return prospectedCards;
	}
	public int getCouncillorOfferedCards() {
		return councillorOfferedCards;
	}
	public int getCouncillorRetainCards() {
		return councillorRetainCards;
	}
	
	
}
