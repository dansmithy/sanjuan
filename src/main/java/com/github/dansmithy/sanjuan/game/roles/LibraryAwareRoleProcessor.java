package com.github.dansmithy.sanjuan.game.roles;

import com.github.dansmithy.sanjuan.game.RoleProcessor;
import com.github.dansmithy.sanjuan.model.*;
import com.github.dansmithy.sanjuan.model.input.PlayChoice;
import com.github.dansmithy.sanjuan.model.update.GameUpdater;

public class LibraryAwareRoleProcessor implements RoleProcessor {

	private RoleProcessor roleProcessor;

	public LibraryAwareRoleProcessor(RoleProcessor roleProcessor) {
		super();
		this.roleProcessor = roleProcessor;
	}

	@Override
	public Role getRole() {
		return roleProcessor.getRole();
	}

	@Override
	public void initiateNewPlay(GameUpdater gameUpdater) {
        setUseLibraryForPlayer(gameUpdater.getGame(), gameUpdater.getNewRound(), gameUpdater.getNewPlayer());
		roleProcessor.initiateNewPlay(gameUpdater);
	}

    @Override
    public void makeChoice(GameUpdater gameUpdater, PlayChoice playChoice) {
        setUseLibraryForPlayer(gameUpdater.getGame(), gameUpdater.getCurrentRound(), gameUpdater.getCurrentPlayer());
        roleProcessor.makeChoice(gameUpdater, playChoice);
    }

    private void setUseLibraryForPlayer(Game game, Round round, Player player) {
        if (game.getPlayers().size() >= 3 || !round.getGovernor().equals(player.getName())) {
            return;
        }
        int phaseNumberToUseLibraryOn = round.getPhases().get(0).getLeadPlayerUsedLibrary() ? 1 : 3;
        if (round.getPhaseNumber() != phaseNumberToUseLibraryOn) {
            player.getPlayerNumbers().setLibraryNotUsed();
        }
    }

}
