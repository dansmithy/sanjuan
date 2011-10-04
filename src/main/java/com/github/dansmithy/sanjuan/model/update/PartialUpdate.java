package com.github.dansmithy.sanjuan.model.update;

public class PartialUpdate {

	private String updatePath;
	private Object updateObject;
	
	public PartialUpdate(String updatePath, Object updateObject) {
		super();
		this.updatePath = updatePath;
		this.updateObject = updateObject;
	}
	
	public String getUpdatePath() {
		return updatePath;
	}
	public Object getUpdateObject() {
		return updateObject;
	}
	
	
}
