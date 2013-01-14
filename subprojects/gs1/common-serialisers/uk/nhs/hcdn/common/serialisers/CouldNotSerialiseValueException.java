/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotSerialiseValueException extends AbstractDataException
{
	public CouldNotSerialiseValueException(@NotNull final ValueSerialisable value, @NotNull final AbstractDataException cause)
	{
		super(format(ENGLISH, "Could not serialise value %1$s", value), cause);
	}
}
