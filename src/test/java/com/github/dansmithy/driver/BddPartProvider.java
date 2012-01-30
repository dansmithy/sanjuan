package com.github.dansmithy.driver;

import static com.github.dansmithy.json.JsonMatchers.*;
import static org.hamcrest.Matchers.*;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Assert;

import com.github.dansmithy.bdd.BddPart;
import com.github.dansmithy.bdd.SimpleBddParts;
import com.github.restdriver.serverdriver.Matchers;
import com.github.restdriver.serverdriver.http.response.Response;
import com.jayway.jsonpath.JsonPath;

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
				Assert.assertThat(String.format("Response code is not 200 (body content is %s)", context.getLastResponse().asText()), context
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
	

	public static BddPart<GameDriver> verifyJsonPath(final String jsonPath, final Matcher<Object> matcher) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertThat(context.getLastResponse().asJson(), Matchers.hasJsonPath(jsonPath, matcher));
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
	
	public static String loggedInAs(String username) {
		return username;
	}
	
	public static BddPart<GameDriver> getGame(final String username, final long gameId) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(username).getGame(gameId);
				context.setLastResponse(response);
			}
		};
	}

	public static BddPart<GameDriver> getGamesFor(final String username, final String loggedInUser) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(loggedInUser).getGetGamesFor(username);
				context.setLastResponse(response);
			}
		};
	}
	
	public static BddPart<GameDriver> getGamesInState(final String username, final String state) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(username).getGamesInState(state);
				context.setLastResponse(response);
			}
		};
	}	
	
	public static BddPart<GameDriver> getAllGames(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getSession(username).getAllGames();
				context.setLastResponse(response);
			}
		};
	}
	
	public static BddPart<GameDriver> noSetUp() {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				// nothing
			}
		};
	}			
	
	public static BddPart<GameDriver> getCards() {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getCards();
				context.setLastResponse(response);
			}
		};
	}	
	
	public static BddPart<GameDriver> getCardTypes() {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response response = context.getCardTypes();
				context.setLastResponse(response);
			}
		};
	}		
	
	public static BddPart<GameDriver> lastResponseRememberedAs(final String rememberedResponseKey) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				context.rememberLastResponse(rememberedResponseKey);
			}
		};
	}
	
	public static BddPart<GameDriver> verifyResponse(final ResponseMatcher matcher) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Assert.assertTrue(matcher.getPurpose(), matcher.matches(context));
			}
		};
	}	
	
	public static RememberedResponseContextOperator rememberedAs(final String rememberedResponseKey) {
		return new RememberedResponseContextOperator(rememberedResponseKey);
	}
	public static ResponseMatcher containsGameFoundInResponse(final ContextOperator<Response, GameDriver> contextOperator) {
		return new ResponseMatcher() {
			
			@Override
			public boolean matches(GameDriver context) {
				Response lastResponse = context.getLastResponse();
				Response rememberedResponse = contextOperator.getFromContext(context);
				Integer gameId = rememberedResponse.asJson().path("gameId").asInt();
				try {
					List<Integer> ids = JsonPath.read(lastResponse.getContent(), "$[*].gameId");
					return ids.contains(gameId);
				} catch (ParseException e) {
					return false;
				}
			}
			
			@Override
			public String getPurpose() {
				return "Response did not contain expected game.";
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
				String gameId = context.getSession(username).getGameId();
				context.getAdminSession().orderDeck(gameId, order);
			}
		};
	}

	public static BddPart<GameDriver> orderTariffOwnedBy(final String username,
			final List<Integer> order) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				String gameId = context.getSession(username).getGameId();
				context.getAdminSession().orderTariff(gameId, order);
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
	
	public static BddPart<GameDriver> copyGameIdBetweenUsers(final String existingPlayer, final String otherUser) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				String gameId = context.getSession(existingPlayer).getGameId();
				context.getSession(otherUser).setGameId(gameId);
			}
		};
	}
	
	public static BddPart<GameDriver> getGameOwnedBySomebodyElse(final String username, final String owner) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				String gameId = context.getSession(owner).getGameId();
				Response actualResponse = context.getSession(username)
						.getGame(Long.valueOf(gameId));
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

	public static BddPart<GameDriver> gameDeletedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.deleteGame(context.getSession(username).getGameId());
				context.setLastResponse(actualResponse);
			}
		};
	}
	
	public static BddPart<GameDriver> gameQuitBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.quitGame(username);
				context.setLastResponse(actualResponse);
			}
		};
	}	

	
	public static BddPart<GameDriver> gameAbandonedBy(final String username) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.abandonGame();
				context.setLastResponse(actualResponse);
			}
		};
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
	
	public static BddPart<GameDriver> userMakesGovernorPlay(final String username,
			final String urlData, final String postJson) {
		return new BddPart<GameDriver>() {
			@Override
			public void execute(GameDriver context) {
				Response actualResponse = context.getSession(username)
						.makesGovernorPlay(urlData, postJson);
				context.setLastResponse(actualResponse);
			}
		};
	}	
}
