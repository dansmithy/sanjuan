package com.github.dansmithy.sanjuan.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

	public static <T> boolean hasDuplicates(Collection<T> collection) {
		Set<T> set = new HashSet<T>(collection);
		return set.size() != collection.size();
	}
}
