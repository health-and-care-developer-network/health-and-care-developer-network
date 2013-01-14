/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ToStringGenerator<T>
{
	void register(@NotNull final Map<Class<?>, ToStringGenerator<?>> register);

	@NotNull
	String toString(@NotNull final T value);
}
