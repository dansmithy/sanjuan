package com.github.dansmithy.sanjuan.model.update;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

import com.github.dansmithy.sanjuan.model.Game;
import com.github.dansmithy.sanjuan.model.Player;
import com.github.dansmithy.sanjuan.model.builder.CardFactory;
import com.github.dansmithy.sanjuan.model.builder.TariffBuilder;

public class GameUpdaterTest {

	@Test
	public void getCorrectCurrentPlayerOnGameStart() {
		
		// given
		Game game = new Game();
		game.addPlayer(new Player("alice"));
		game.addPlayer(new Player("bob"));
		game.startPlaying(new CardFactory(), new TariffBuilder());
		GameUpdater updater = new GameUpdater(game);
		
		// when
		Player actualPlayer = updater.getCurrentPlayer();
		
		// then
		Assert.assertThat(actualPlayer.getName(), is(equalTo("alice")));
	}
}
