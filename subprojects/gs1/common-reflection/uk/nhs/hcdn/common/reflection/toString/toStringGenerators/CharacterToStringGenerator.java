/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.UK;

public final class CharacterToStringGenerator extends AbstractToStringGenerator<Character>
{
	@NotNull
	public static final ToStringGenerator<?> CharacterToStringGeneratorInstance = new CharacterToStringGenerator();

	private CharacterToStringGenerator()
	{
		super(Character.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull final Character value)
	{
		@NonNls final String format = "0x%1$04X";
		return format(UK, format, (int) (Character) value);
	}
}
