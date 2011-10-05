package com.github.dansmithy.sanjuan.game;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.BuildingType;
import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;

@Named
public class GameService {

	private TariffBuilder tariffBuilder;
	private CardFactory cardFactory;
	private final CalculationService calculationService;
	
	@Inject
	public GameService(TariffBuilder tariffBuilder, CardFactory cardFactory, CalculationService calculationService) {
		super();
		this.tariffBuilder = tariffBuilder;
		this.cardFactory = cardFactory;
		this.calculationService = calculationService;
	}

	public Game startGame(Game game) {
		game.startPlaying(cardFactory, tariffBuilder);
		return game;
	}
	
	public void doCalculations(Game game) {
		for (Player player : game.getPlayers()) {
			calculationService.processPlayer(player);
		}
	}
}
