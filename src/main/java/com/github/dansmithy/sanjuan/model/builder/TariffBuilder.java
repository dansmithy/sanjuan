package com.github.dansmithy.sanjuan.model.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.model.Tariff;

@Named
public class TariffBuilder {

	private List<Tariff> createTariffList() {
		return Arrays.asList(
			new Tariff(new int[] { 1, 1, 1, 2, 2 }),
			new Tariff(new int[] { 1, 1, 2, 2, 2 }),
			new Tariff(new int[] { 1, 1, 2, 2, 3 }),
			new Tariff(new int[] { 1, 2, 2, 2, 3 }),
			new Tariff(new int[] { 1, 2, 2, 3, 3 })
		);
	}
	
	public List<Tariff> createRandomTariff() {
		List<Tariff> tariffs = createTariffList();
		Collections.shuffle(tariffs);
		return Collections.unmodifiableList(tariffs);
	}
}
