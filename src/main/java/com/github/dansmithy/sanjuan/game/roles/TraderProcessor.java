package com.github.dansmithy.sanjuan.game.roles;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

@Named
public class TraderProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.BUILDER;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		// do nothing
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		// do nothing
	}



}
