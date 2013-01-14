/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotEncodeDataException extends AbstractDataException
{
	public CouldNotEncodeDataException(@NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Could not encode data because %1$s", because));
	}
}
