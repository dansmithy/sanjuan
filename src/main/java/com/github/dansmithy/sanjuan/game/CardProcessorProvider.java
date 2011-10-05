package com.github.dansmithy.sanjuan.game;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.ListableBeanFactory;

@Named
public class CardProcessorProvider {

	private Map<String, CardProcessor> processors = new HashMap<String, CardProcessor>();

	@Inject
	public CardProcessorProvider(ListableBeanFactory beanFactory) {
		Map<String, CardProcessor> map = beanFactory.getBeansOfType(CardProcessor.class);
		for (CardProcessor processor : map.values()) {
			processors.put(processor.getProcessorType(), processor);
		}
	}

	public CardProcessor getProcessor(String processorType) {
		return processors.get(processorType);
	}
}
