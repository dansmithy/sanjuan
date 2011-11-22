package com.github.dansmithy.sanjuan.game.roles;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Play;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

public class SkipAwareRoleProcessor implements RoleProcessor {

	private RoleProcessor roleProcessor;
	
	public SkipAwareRoleProcessor(RoleProcessor roleProcessor) {
		super();
		this.roleProcessor = roleProcessor;
	}

	@Override
	public Role getRole() {
		return roleProcessor.getRole();
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		roleProcessor.initiateNewPlay(gameUpdater);
	}

	@Override
	public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
		if (!playChoice.isSkipRequested()) {
			roleProcessor.makeChoice(gameUpdater, playChoice);
		}
		Play play = gameUpdater.getCurrentPlay();
		gameUpdater.completedPlay(play, playChoice);
	}

}
