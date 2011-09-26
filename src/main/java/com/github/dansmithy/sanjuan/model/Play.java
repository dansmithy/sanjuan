package com.github.dansmithy.sanjuan.model;

import java.util.HashMap;
import java.util.Map;

public class Play {

	private String player;
	private boolean hasPrivilige;
	
	private Map<String, Object> detail = new HashMap<String, Object>();

	public String getPlayer() {
		return player;
	}

	public boolean isHasPrivilige() {
		return hasPrivilige;
	}
	
}
