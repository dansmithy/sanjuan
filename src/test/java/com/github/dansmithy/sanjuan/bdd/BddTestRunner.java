package com.github.dansmithy.sanjuan.bdd;

/**
 * A test runner will expect a given, event and an outcome and can choose to execute the test.
 * 
 * @param <T>
 *            the type used for the context.
 */
public interface BddTestRunner<T> {

	void runTest(BddPart<T> given, BddPart<T> event, BddPart<T> outcome);
}
