package com.github.dansmithy.bdd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleBddParts<T> implements BddParts<T> {

	private List<BddPart<T>> bddParts = new ArrayList<BddPart<T>>();
	
	public SimpleBddParts(BddPart<T> bddPart) {
		bddParts.add(bddPart);
	}
	
	@Override
	public Iterator<BddPart<T>> iterator() {
		return bddParts.iterator();
	}

	@Override
	public void execute(T context) {
		for (BddPart<T> part : this) {
			executePart(part, context);
		}
	}

	protected void executePart(BddPart<T> part, T context) {
		part.execute(context);
	}

	@Override
	public BddParts<T> and(BddPart<T> extraPart) {
		bddParts.add(extraPart);
		return this;
	}

	@Override
	public BddParts<T> and(List<BddPart<T>> extraParts) {
		for (BddPart<T> part : extraParts) {
			and(part);
		}
		return this;
	}

}
