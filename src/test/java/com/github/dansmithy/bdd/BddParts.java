package com.github.dansmithy.bdd;

/**
 * Allows iteration of a collection of {@link BddPart}s. Also is a {@link BddPart} itself.
 * 
 * @param <T>
 *            the type used for the context.
 */
public interface BddParts<T> extends Iterable<BddPart<T>>, BddPart<T> {

	BddParts<T> and(BddPart<T> extraPart);

}
