package com.github.dansmithy.sanjuan.model.input;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class PlayChoice {

	private Integer build;
	//Spring Mongo integration can't cope with arrays here if the array is empty (which it may be)
	private List<Integer> payment = new ArrayList<Integer>();
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
	public Integer[] getPaymentAsArray() {
		return payment.toArray(new Integer[payment.size()]);
	}
	
}
