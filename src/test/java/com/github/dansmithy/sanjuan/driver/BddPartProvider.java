package com.github.dansmithy.sanjuan.driver;

import static com.github.dansmithy.sanjuan.json.JsonMatchers.containsJson;
import static com.github.dansmithy.sanjuan.json.JsonMatchers.whenTranslatedBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.HttpURLConnection;
import java.util.List;

import org.junit.Assert;

import com.github.dansmithy.sanjuan.bdd.BddPart;
import com.github.restdriver.serverdriver.http.response.Response;

public class BddPartProvider {

	public static BddPart<BddContext> verifySuccessfulResponseContains(final String responseData) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Assert.assertThat("Response code is not 200", context.getLastResponse().getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
				Assert.assertThat(context.getLastResponse().asText(), containsJson(responseData, whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}	
	
//	public static BddPart<BddContext> verifySuccessful() {
//		return new BddPart<BddContext>() {
//			@Override
//			public void execute(BddContext context) {
//				Assert.assertThat(context.getLastResponse().getStatusCode(), is(equalTo(HttpURLConnection.HTTP_OK)));
//			}
//		};
//	}	
	
	public static BddPart<BddContext> verifyResponseCodeIs(final int code) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Assert.assertThat("Response code is not as expected", context.getLastResponse().getStatusCode(), is(equalTo(code)));
			}
		};
	}	
	
	public static BddPart<BddContext> userExists(final String data) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				context.getAdminSession().createUser(data);
			}
		};
	}
	
	public static BddPart<BddContext> userAuthenticated(final String username) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				context.loginUser(username);
			}
		};
	}
	
	public static BddPart<BddContext> userExistsAndAuthenticated(final String username) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				context.getAdminSession().createUser(String.format("username : %s", username));
				context.loginUser(username);
			}
		};
	}	
	
	public static BddPart<BddContext> orderDeckOwnedBy(final String username, final List<Integer> order) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				context.getAdminSession().addTranslatedValues(DeckOrder.deckShorthand());
				context.getAdminSession().orderDeck(context.getSession(username).getGameId(), order);
			}
		};
	}		
	
	public static BddPart<BddContext> gameOwnedByContains(final String owner, final String gameJson) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response response = context.getSession(owner).getGame();
				Assert.assertThat(response.asText(), containsJson(gameJson, whenTranslatedBy(context.getTranslatedValues())));
			}
		};
	}	
	
	public static BddPart<BddContext> gameCreatedBy(final String username) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response actualResponse = context.getSession(username).createGame(String.format("username : %s", username));
				context.setLastResponse(actualResponse);
			}
		};
	}
	
	public static BddPart<BddContext> gameOwnedByJoinedBy(final String owner, final String joiner) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response actualResponse = context.getSession(joiner).joinGame(context.getSession(owner).getGameId(), String.format("username : %s", joiner));
				context.setLastResponse(actualResponse);
			}
		};
	}
	
	public static BddPart<BddContext> gameStartedBy(final String username) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response actualResponse = context.getSession(username).startGame();
				context.setLastResponse(actualResponse);
			}
		};
	}	
	
	public static BddPart<BddContext> roleChosenBy(final String username, final String urlData, final String postData) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response actualResponse = context.getSession(username).chooseRole(urlData, postData);
				context.setLastResponse(actualResponse);
			}
		};
	}	
	
	public static BddPart<BddContext> userPlays(final String username, final String urlData, final String postData) {
		return new BddPart<BddContext>() {
			@Override
			public void execute(BddContext context) {
				Response actualResponse = context.getSession(username).makePlayChoice(urlData, postData);
				context.setLastResponse(actualResponse);
			}
		};
	}		
}
