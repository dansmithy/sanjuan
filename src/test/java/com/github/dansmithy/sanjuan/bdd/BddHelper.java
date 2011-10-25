package com.github.dansmithy.sanjuan.bdd;

/**
 * Provides syntactic sugar for writing tests in style of: given ... when ... then ... 
 *
 */
public class BddHelper {
	
	public static <T> BddParts<T> given(BddPart<T> given) {
		return new SimpleBddParts<T>(given);
	}
	
	public static <T> BddParts<T> when(BddPart<T> event) {
		return new SimpleBddParts<T>(event);
	}	
	
	public static <T> BddParts<T> then(BddPart<T> outcome) {
		return new SimpleBddParts<T>(outcome);
	}
}
