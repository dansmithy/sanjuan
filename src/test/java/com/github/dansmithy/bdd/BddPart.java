package com.github.dansmithy.bdd;

/**
 * Represents an executable part of the BDD test. Can either be a "given" clause (eg. the contexts), a "when" clause (the event) or a "then" clause
 * (the outcome).
 * 
 * @param <T>
 *            the type used for the context.
 */
public interface BddPart<T> {

	void execute(T context);

}
