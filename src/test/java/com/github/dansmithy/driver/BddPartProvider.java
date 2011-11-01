package com.github.dansmithy.driver;

import static com.github.dansmithy.json.JsonMatchers.containsJson;
import static com.github.dansmithy.json.JsonMatchers.whenTranslatedBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.HttpURLConnection;
import java.util.List;

import org.junit.Assert;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.SimpleBddParts;
import com.github.restdriver.serverdriver.http.response.Response;

public class BddPartProvider {

	public static BddPart<GameDriver> verifySuccessfulResponseContains(final String responseData) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertThat(context.getLastResponse().asText(), containsJson(responseData, whenTranslatedBy(context.getTranslatedValues())));
				Assert.assertThat("Response code is not 200", context.getLastResponse().getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
			}
		};
	}	
	
//	public static BddPart<GameDriver> verifySuccessful() {
//		return new BddPart<GameDriver>() {
//			@Override
//			public void execute(GameDriver context) {
//				Assert.assertThat(context.getLastResponse().getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
//			}
//		};
//	}	
	
	public static BddPart<GameDriver> verifyResponseCodeIs(final int code) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertThat(String.format("Response code is not as expected for response with content [%s].", context.getLastResponse().asText()), context.getLastResponse().getStatusCode(), is(equalTo(code)));
			}
		};
	}	
	
	public static BddPart<GameDriver> userExists(final String data) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.getAdminSession().createUser(data);
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
	
	public static BddPart<GameDriver> userExistsAndAuthenticated(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.getAdminSession().createUser(username);
				context.loginUser(username);
			}
		};
	}	
	
	public static BddPart<GameDriver> orderDeckOwnedBy(final String username, final List<Integer> order) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.getAdminSession().addTranslatedValues(DeckOrder.deckShorthand());
				context.getAdminSession().orderDeck(context.getSession(username).getGameId(), order);
			}
		};
	}		
	
	public static BddPart<GameDriver> gameOwnedByContains(final String owner, final String gameJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(owner).getGame();
				Assert.assertThat(response.asText(), containsJson(gameJson, whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}	
	
	public static BddPart<GameDriver> gameCreatedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username).createGame(String.format("username : %s", username));
				context.setLastResponse(actualResponse);
			}
		};
	}
	
	public static BddPart<GameDriver> gameOwnedByJoinedBy(final String owner, final String joiner) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				String gameId = context.getSession(owner).getGameId();
				Response actualResponse = context.getSession(joiner).joinGame(gameId, String.format("username : %s", joiner));
				context.setLastResponse(actualResponse);
			}
		};
	}
	
	public static BddPart<GameDriver> gameStartedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username).startGame();
				context.setLastResponse(actualResponse);
			}
		};
	}	
	
	public static BddPart<GameDriver> gameBegunWithTwoPlayers(final String player1, final String player2) {
		return new SimpleBddParts<GameDriver>(userExistsAndAuthenticated(player1))
						.and(userExistsAndAuthenticated(player2))
						.and(gameCreatedBy(player1))
						.and(orderDeckOwnedBy(player1, DeckOrder.Order1))
						.and(gameOwnedByJoinedBy(player1, player2))
						.and(gameStartedBy(player1));
	}
	
	public static BddPart<GameDriver> roleChosenBy(final String username, final String urlData, final String postData) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username).chooseRole(urlData, postData);
				context.setLastResponse(actualResponse);
			}
		};
	}	
	
	public static BddPart<GameDriver> userPlays(final String username, final String urlData, final String postJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username).makePlayChoice(urlData, postJson);
				context.setLastResponse(actualResponse);
			}
		};
	}		
}
