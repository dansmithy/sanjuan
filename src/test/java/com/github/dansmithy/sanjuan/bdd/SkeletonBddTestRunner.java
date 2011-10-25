package com.github.dansmithy.sanjuan.bdd;

public abstract class SkeletonBddTestRunner<T> implements BddTestRunner<T> {

	@Override
	public void runTest(BddPart<T> given, BddPart<T> event, BddPart<T> outcome) {
		T context = createContext();
		beforeTest(context);
		try {
			given.execute(context);
			event.execute(context);
			outcome.execute(context);
		} finally {
			afterTest(context);
		}
	}

	protected abstract T createContext();
	
	protected void beforeTest(T context) {
		// empty
	}
	
	protected void afterTest(T context) {
		// empty
	}
	
}
