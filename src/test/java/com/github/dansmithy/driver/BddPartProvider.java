package com.github.dansmithy.driver;

import static com.github.dansmithy.json.JsonMatchers.containsJson;
import static com.github.dansmithy.json.JsonMatchers.whenTranslatedBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.SimpleBddParts;
import com.github.restdriver.serverdriver.http.response.Response;

public class BddPartProvider {

	public static BddPart<GameDriver> nothingHappens() {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				// nothing
			}
		};
	}
	
	public static BddPart<GameDriver> verifySuccessfulResponseContains(
			final String responseData) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertNotNull("Expected successful response to have been saved, but none found.", context.getLastResponse());
				Assert.assertThat("Response code is not 200", context
						.getLastResponse().getStatusCode(),
						is(equalTo(HttpURLConnection.HTTP_OK)));
				Assert.assertThat(
						context.getLastResponse().asText(),
						containsJson(responseData,
								whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}

	public static BddPart<GameDriver> verifyResponseContains(
			final String responseData) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertThat(
						context.getLastResponse().asText(),
						containsJson(responseData,
								whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}

	// public static BddPart<GameDriver> verifySuccessful() {
	// return new BddPart<GameDriver>() {
	// @Override
	// public void execute(GameDriver context) {
	// Assert.assertThat(context.getLastResponse().getStatusCode(),
	// is(equalTo(HttpURLConnection.HTTP_OK)));
	// }
	// };
	// }

	public static BddPart<GameDriver> verifyResponseCodeIs(final int code) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertThat(
						String.format(
								"Response code is not as expected for response with content [%s].",
								context.getLastResponse().asText()), context
								.getLastResponse().getStatusCode(),
						is(equalTo(code)));
			}
		};
	}

	public static BddPart<GameDriver> userExists(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.createUser(username);
			}
		};
	}
	

	public static BddPart<GameDriver> updateUser(final String username, final String userJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getAdminSession().updateUser(username, userJson);
				context.setLastResponse(actualResponse);
			}
		};		
	}	
	
	public static BddPart<GameDriver> getUsers() {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getAdminSession().getUsers();
				context.setLastResponse(actualResponse);
			}
		};		
	}		

	public static BddPart<GameDriver> userAuthenticated(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.loginUser(username);
			}
		};
	}

	public static BddPart<GameDriver> userExistsAndAuthenticated(
			final String username) {
		return new SimpleBddParts<GameDriver>(userExists(username))
				.and(userAuthenticated(username));
	}

	public static BddPart<GameDriver> orderDeckOwnedBy(final String username,
			final List<Integer> order) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.getAdminSession().addTranslatedValues(
						DeckOrder.deckShorthand());
				context.getAdminSession().orderDeck(
						context.getSession(username).getGameId(), order);
			}
		};
	}

	public static BddPart<GameDriver> orderTariffOwnedBy(final String username,
			final List<Integer> order) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.getAdminSession().orderTariff(
						context.getSession(username).getGameId(), order);
			}
		};
	}

	public static BddPart<GameDriver> gameOwnedByContains(final String owner,
			final String gameJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(owner).getGame();
				Assert.assertThat(
						response.asText(),
						containsJson(gameJson,
								whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}

	public static BddPart<GameDriver> gameCreatedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.createGame(String.format("username : %s", username));
				context.setLastResponse(actualResponse);
			}
		};
	}

	public static BddPart<GameDriver> gameOwnedByJoinedBy(final String owner,
			final String joiner) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				String gameId = context.getSession(owner).getGameId();
				Response actualResponse = context.getSession(joiner).joinGame(
						gameId, String.format("username : %s", joiner));
				context.setLastResponse(actualResponse);
			}
		};
	}

	public static BddPart<GameDriver> gameStartedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.startGame();
				context.outputGamePlayers();
				context.setLastResponse(actualResponse);
			}
		};
	}

	public static BddPart<GameDriver> gameBegunWithTwoPlayers(
			final String player, final String player2) {
		return gameBegunWithTwoPlayers(player, player2, DeckOrder.Order1);
	}

	public static BddPart<GameDriver> gameBegunWithTwoPlayers(
			final String player, final String player2, List<Integer> deckOrder) {
		return new SimpleBddParts<GameDriver>(
				userExistsAndAuthenticated(player))
				.and(userExistsAndAuthenticated(player2))
				.and(gameCreatedBy(player))
				.and(orderDeckOwnedBy(player, deckOrder))
				.and(orderTariffOwnedBy(player, Arrays.asList(4, 3, 2, 1, 0)))
				.and(gameOwnedByJoinedBy(player, player2))
				.and(gameStartedBy(player));
	}

	public static List<Integer> withDeck(List<Integer> deckOrder) {
		return deckOrder;
	}

	public static BddPart<GameDriver> roleChosenBy(final String username,
			final String urlData, final String postData) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.chooseRole(urlData, postData);
				context.setLastResponse(actualResponse);
			}
		};
	}

	public static BddPart<GameDriver> userPlays(final String username,
			final String urlData, final String postJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.makePlayChoice(urlData, postJson);
				context.setLastResponse(actualResponse);
			}
		};
	}
}
