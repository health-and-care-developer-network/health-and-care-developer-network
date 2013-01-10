package org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ToStringGenerator<T>
{
	void register(@NotNull final Map<Class<?>, ToStringGenerator<?>> register);

	@NotNull
	String toString(@NotNull final T value);
}
