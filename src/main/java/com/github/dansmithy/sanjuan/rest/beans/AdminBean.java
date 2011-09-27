package com.github.dansmithy.sanjuan.rest.beans;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.rest.jaxrs.AdminResource;

@Named
public class AdminBean implements AdminResource {

	@Override
	public Map<String, String> mongoDetails() {
		Map<String, String> map = new HashMap<String, String>();
		String mongoUri = System.getenv("MONGOLAB_URI");
		map.put("type", "mongo");
		map.put("uri", mongoUri);
		return map;
	}

}
