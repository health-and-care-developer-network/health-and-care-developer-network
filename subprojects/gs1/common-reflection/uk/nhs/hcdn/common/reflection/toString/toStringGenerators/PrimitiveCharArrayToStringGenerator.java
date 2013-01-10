package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveCharArrayToStringGenerator extends AbstractToStringGenerator<char[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveCharArrayToStringGeneratorInstance = new PrimitiveCharArrayToStringGenerator();

	private PrimitiveCharArrayToStringGenerator()
	{
		super(char[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final char[] value)
	{
		return Arrays.toString(value);
	}
}
