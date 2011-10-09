package com.github.dansmithy.sanjuan.game.roles;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

public class CouncillorProcessor implements RoleProcessor {

	@Override
	public Role getRole() {
		return Role.COUNCILLOR;
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
		// do nothing
	}

}
