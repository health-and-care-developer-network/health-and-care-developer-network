/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.UK;

public final class ByteToStringGenerator extends AbstractToStringGenerator<Byte>
{
	@NotNull
	public static final ToStringGenerator<?> ByteToStringGeneratorInstance = new ByteToStringGenerator();

	private ByteToStringGenerator()
	{
		super(Byte.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull final Byte value)
	{
		@NonNls final String format = "0x%1$02X";
		return format(UK, format, (Byte) value);
	}
}
