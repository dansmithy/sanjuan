package com.github.dansmithy.sanjuan.game.cards;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.game.BonusCardMatcher;
import com.github.dansmithy.sanjuan.game.PlayerNumbers;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.Role;

@Named
public class PoorHouseCardProcessor extends StandardCardProcessor {

	private static final BonusCardMatcher MATCHER = new PoorHouseBonusCardMatcher();
	
	@Override
	public String getProcessorType() {
		return "poorhouse";
	}

	@Override
	public void determinePrivileges(PlayerNumbers playerNumbers) {
		playerNumbers.addBonusCardMatcher(MATCHER);
	}
	
	private static class PoorHouseBonusCardMatcher implements BonusCardMatcher {

		@Override
		public int getBonusCardMatches(Player player, Role role) {
			if (Role.BUILDER.equals(role) && player.getHandCards().size() <= 1) {
				return 1;
			}
			return 0;
		}
	}

}
