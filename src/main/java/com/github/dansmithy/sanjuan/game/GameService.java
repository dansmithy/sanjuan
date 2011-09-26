package com.github.dansmithy.sanjuan.game;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;

@Named
public class GameService {

	private TariffBuilder tariffBuilder;
	private CardFactory cardFactory;
	
	@Inject
	public GameService(TariffBuilder tariffBuilder, CardFactory cardFactory) {
		super();
		this.tariffBuilder = tariffBuilder;
		this.cardFactory = cardFactory;
	}

	public Game startGame(Game game) {
		game.startPlaying(cardFactory, tariffBuilder);
		return game;
	}
}
