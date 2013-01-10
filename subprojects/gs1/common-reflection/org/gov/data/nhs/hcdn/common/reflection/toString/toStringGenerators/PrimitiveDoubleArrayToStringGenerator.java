package org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveDoubleArrayToStringGenerator extends AbstractToStringGenerator<double[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveDoubleArrayToStringGeneratorInstance = new PrimitiveDoubleArrayToStringGenerator();

	private PrimitiveDoubleArrayToStringGenerator()
	{
		super(double[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final double[] value)
	{
		return Arrays.toString(value);
	}
}
