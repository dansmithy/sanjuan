package com.github.dansmithy.sanjuan.rest.beans;

import javax.inject.Named;

import com.github.dansmithy.sanjuan.rest.jaxrs.ViewsResource;

@Named
public class ViewsBean implements ViewsResource {

	@Override
	public Data getFiltered() {
		return Data.MAIN;
	}

	@Override
	public Data getFull() {
		return Data.MAIN;
	}

}
