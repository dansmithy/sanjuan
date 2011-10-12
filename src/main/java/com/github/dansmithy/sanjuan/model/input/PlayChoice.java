package com.github.dansmithy.sanjuan.model.input;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PlayChoice {

	private Integer build;
	//Spring Mongo integration can't cope with arrays here if the array is empty (which it may be), so we have to use Collections
	private List<Integer> payment;
	private Boolean skip = null;
	private List<Integer> councilDiscarded;
	private List<Integer> productionFactories;
	
	public Integer getBuild() {
		return build;
	}
	public void setBuild(Integer build) {
		this.build = build;
	}
	public List<Integer> getPayment() {
		return payment;
	}
	public void setPayment(List<Integer> payment) {
		this.payment = payment;
	}
	
	@JsonIgnore
	public List<Integer> getCouncilDiscarded() {
		return councilDiscarded;
	}
	public void setCouncilDiscarded(List<Integer> councilDiscarded) {
		this.councilDiscarded = councilDiscarded;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}
	
	public Boolean getSkip() {
		return skip;
	}
	@JsonIgnore
	public Integer[] getPaymentAsArray() {
		return payment.toArray(new Integer[payment.size()]);
	}
	
	public List<Integer> getProductionFactories() {
		return productionFactories;
	}
	public void setProductionFactories(List<Integer> productionFactories) {
		this.productionFactories = productionFactories;
	}

	
	
}
