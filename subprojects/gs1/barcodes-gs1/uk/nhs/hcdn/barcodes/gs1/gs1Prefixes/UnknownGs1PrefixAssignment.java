/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hcdn.barcodes.Digits.digits;

public final class UnknownGs1PrefixAssignment extends AbstractToString implements Gs1PrefixAssignment
{
	@NotNull
	private final Digits digits;

	public UnknownGs1PrefixAssignment(final int prefix)
	{
		digits = digits(Integer.toString(prefix));
	}

	@Override
	public boolean isISMN(@NotNull final Digit third, @NotNull final Digit fourth)
	{
		return false;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final UnknownGs1PrefixAssignment that = (UnknownGs1PrefixAssignment) obj;

		if (!digits.equals(that.digits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return digits.hashCode();
	}

	@NotNull
	@Override
	public String actualName()
	{
		return "(Unknown)";
	}
}
