package com.github.dansmithy.sanjuan.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.document.mongodb.mapping.Document;

@Document(collection = "seq")
public class MongoSequence {

	@Id
	private String type;
	
	private Long seq;
	
	public MongoSequence(String type, Long seq) {
		super();
		this.type = type;
		this.seq = seq;
	}

	public MongoSequence() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	
	
	
}
