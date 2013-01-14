/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

public final class ObjectToStringGenerator extends AbstractToStringGenerator<Object>
{
	@NotNull
	public static final ToStringGenerator<?> ObjectToStringGeneratorInstance = new ObjectToStringGenerator();

	private ObjectToStringGenerator()
	{
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final Object value)
	{
		return value.toString();
	}
}
