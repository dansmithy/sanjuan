package com.github.dansmithy.sanjuan.rest.beans;

import org.codehaus.jackson.map.annotate.JsonView;

import com.github.dansmithy.sanjuan.rest.jaxrs.GameViews;

public class Data {

	public static Data MAIN = new Data("amy", "brian", "charlie", "daniel"); 
	
	private String alpha;
	private String beta;
	private String gamma;
	private String delta;
	
	public Data(String alpha, String beta, String gamma, String delta) {
		super();
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.delta = delta;
	}
	
	public String getAlpha() {
		return alpha;
	}
	
	public String getBeta() {
		return beta;
	}
	
	@JsonView(GameViews.Full.class)
	public String getGamma() {
		return gamma;
	}
	public String getDelta() {
		return delta;
	}
	
	
}
