package com.github.dansmithy.bdd;

public abstract class SkeletonBddTestRunner<T> implements BddTestRunner<T> {

	@Override
	public void runTest(BddPart<T> given, BddPart<T> event, BddPart<T> outcome) {
		T context = createContext();
		beforeTest(context);
		try {
			doGiven(given, context);
			doEvent(event, context);
			doOutcome(outcome, context);
		} finally {
			afterTest(context);
		}
	}

	protected void doOutcome(BddPart<T> outcome, T context) {
		outcome.execute(context);
	}

	protected void doEvent(BddPart<T> event, T context) {
		event.execute(context);
	}

	protected void doGiven(BddPart<T> given, T context) {
		given.execute(context);
	}

	protected abstract T createContext();
	
	protected void beforeTest(T context) {
		// empty
	}
	
	protected void afterTest(T context) {
		// empty
	}
	
}
