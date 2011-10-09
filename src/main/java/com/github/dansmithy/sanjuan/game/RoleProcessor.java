package com.github.dansmithy.sanjuan.game;

import com.github.dansmithy.sanjuan.model.Role;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

public interface RoleProcessor {

	Role getRole();

	void initiateNewPlay(GameUpdater gameUpdater);

	void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice);
}
