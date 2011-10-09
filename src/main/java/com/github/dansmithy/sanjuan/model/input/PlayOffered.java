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
	
	

}
