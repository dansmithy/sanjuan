package com.github.dansmithy.sanjuan.json;

public interface TestCompare<A, B> {

	void equalsNoOrphans(A actual, B expected);
}
