/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotWriteValueException extends AbstractDataException
{
	public CouldNotWriteValueException(@NotNull final Object value, @NotNull final AbstractDataException cause)
	{
		super(format(ENGLISH, "Could not write value %1$s", value), cause);
	}

	public CouldNotWriteValueException(@NotNull final Object value, @NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Could not write value %1$s because %2$s", value, because));
	}
}
