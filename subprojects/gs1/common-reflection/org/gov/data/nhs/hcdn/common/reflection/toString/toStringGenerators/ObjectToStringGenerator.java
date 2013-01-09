package org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators;

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
