/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hcdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.*;

public abstract class AbstractCompanyPrefix extends AbstractToString implements ValueSerialisable
{
	@NotNull
	protected final Digits digits;

	protected AbstractCompanyPrefix(@NotNull final Digits digits)
	{
		if (digits.size() < 4)
		{
			throw new IncorrectNumberOfDigitsIllegalStateException(4);
		}
		this.digits = digits;
	}

	@Override
	public final void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		digits.serialiseValue(valueSerialiser);
	}

	@NotNull
	public final Gs1Prefix gs1Prefix()
	{
		return new Gs1Prefix(digits.slice(1, 4));
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

		final AbstractCompanyPrefix that = (AbstractCompanyPrefix) obj;

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
}
