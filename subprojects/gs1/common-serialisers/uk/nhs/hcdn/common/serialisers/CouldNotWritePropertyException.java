/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotWritePropertyException extends AbstractDataException
{
	public CouldNotWritePropertyException(@NonNls @NotNull final String name, @NotNull final Object value, @NotNull final AbstractDataException cause)
	{
		super(format(ENGLISH, "Could not write property %1$s with value %2$s", name, value), cause);
	}

	public CouldNotWritePropertyException(@NonNls @NotNull final String name, @NotNull final AbstractDataException cause)
	{
		super(format(ENGLISH, "Could not write property %1$s with value null", name), cause);
	}

	public CouldNotWritePropertyException(@NonNls @NotNull final String name, @NotNull final Object value, @NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Could not write property %1$s with value %2$s because %3$s", name, value, because));
	}
}
