package com.github.dansmithy.sanjuan.game;

import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;

public interface BonusCardMatcher {

	int getBonusCardMatches(Player player, Role role);
}
