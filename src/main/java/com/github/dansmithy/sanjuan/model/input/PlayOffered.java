package com.github.dansmithy.sanjuan.model.input;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PlayOffered {

	private List<Integer> prospected;
	
	private List<Integer> councilOffered;
	private Integer councilRetainCount;
	private Boolean councilCanDiscardHandCards;
	
	private Integer builderDiscountOnProduction;
	private Integer builderDiscountOnViolet;
	
	private Integer goodsCanProduce;
	private Integer goodsCanTrade;
	private List<Integer> factoriesCanProduce;
	
	public List<Integer> getCouncilOffered() {
		return councilOffered == null ? null : new ArrayList<Integer>(councilOffered);
	}
	public void setCouncilOffered(List<Integer> councilOptions) {
		this.councilOffered = councilOptions;
	}
	public List<Integer> getProspected() {
		return prospected;
	}
	public void setProspected(List<Integer> prospected) {
		this.prospected = prospected;
	}
	public Integer getCouncilRetainCount() {
		return councilRetainCount;
	}
	public void setCouncilRetainCount(Integer councilRetainCount) {
		this.councilRetainCount = councilRetainCount;
	}
	public Boolean isCouncilCanDiscardHandCards() {
		return councilCanDiscardHandCards;
	}
	public void setCouncilCanDiscardHandCards(boolean councilCanDiscardHandCards) {
		this.councilCanDiscardHandCards = councilCanDiscardHandCards;
	}
	public Integer getBuilderDiscountOnProduction() {
		return builderDiscountOnProduction;
	}
	public void setBuilderDiscountOnProduction(Integer builderDiscountOnProduction) {
		this.builderDiscountOnProduction = builderDiscountOnProduction;
	}
	public Integer getBuilderDiscountOnViolet() {
		return builderDiscountOnViolet;
	}
	public void setBuilderDiscountOnViolet(Integer builderDiscountOnViolet) {
		this.builderDiscountOnViolet = builderDiscountOnViolet;
	}
	public Integer getGoodsCanProduce() {
		return goodsCanProduce;
	}
	public void setGoodsCanProduce(Integer goodsCanProduce) {
		this.goodsCanProduce = goodsCanProduce;
	}
	public List<Integer> getFactoriesCanProduce() {
		return factoriesCanProduce;
	}
	public void setFactoriesCanProduce(List<Integer> factoriesCanProduce) {
		this.factoriesCanProduce = factoriesCanProduce;
	}
	public Integer getGoodsCanTrade() {
		return goodsCanTrade;
	}
	public void setGoodsCanTrade(Integer goodsCanTrade) {
		this.goodsCanTrade = goodsCanTrade;
	}

}
