package com.github.dansmithy.sanjuan.model;

import java.util.HashMap;
import java.util.Map;

public class Play {

	private String player;
	private boolean hasPrivilige;
	private PlayState state = PlayState.AWAITING_INPUT;
	
	private Map<String, Object> detail = new HashMap<String, Object>();

	public Play() {
		super();
	}
	
	public Play(String player, boolean hasPrivilige) {
		super();
		this.player = player;
		this.hasPrivilige = hasPrivilige;
	}

	public String getPlayer() {
		return player;
	}

	public boolean isHasPrivilige() {
		return hasPrivilige;
	}

	public PlayState getState() {
		return state;
	}
	
	
}
