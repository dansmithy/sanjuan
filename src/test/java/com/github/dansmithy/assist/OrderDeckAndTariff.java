package com.github.dansmithy.assist;

import java.util.Arrays;

import org.junit.Test;

import com.github.dansmithy.driver.DeckOrder;
import com.github.dansmithy.driver.DefaultValues;
import com.github.dansmithy.driver.GameDriver;
import com.github.dansmithy.rest.GameRestDriver;

public class OrderDeckAndTariff {

	private GameDriver driver = new GameRestDriver(DefaultValues.BASE_URI, DefaultValues.ADMIN_USERNAME, DefaultValues.ADMIN_PASSWORD);
	private String gameId = "5558";
	
	@Test
	public void orderDeckAndTariff() {

		driver.getAdminSession().orderDeck(gameId, DeckOrder.Order1);
		driver.getAdminSession().orderTariff(gameId, Arrays.asList(4, 3, 2, 1, 0));
	}
		

}
