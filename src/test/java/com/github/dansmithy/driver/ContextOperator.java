package com.github.dansmithy.driver;

public interface ContextOperator<T, E> {

	T getFromContext(E context);
}
