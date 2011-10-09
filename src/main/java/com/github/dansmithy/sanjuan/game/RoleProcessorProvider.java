package com.github.dansmithy.sanjuan.game;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.ListableBeanFactory;

import com.github.dansmithy.sanjuan.model.Role;

@Named
public class RoleProcessorProvider {

	private Map<Role, RoleProcessor> processors = new HashMap<Role, RoleProcessor>();

	@Inject
	public RoleProcessorProvider(ListableBeanFactory beanFactory) {
		Map<String, RoleProcessor> map = beanFactory.getBeansOfType(RoleProcessor.class);
		for (RoleProcessor processor : map.values()) {
			processors.put(processor.getRole(), processor);
		}
	}

	public RoleProcessor getProcessor(Role role) {
		return processors.get(role);
	}
}
