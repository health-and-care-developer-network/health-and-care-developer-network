/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.T1;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;

public abstract class AbstractCheckDigitNumber<F extends KeyFormat> extends AbstractToString implements CheckDigitNumber
{
	@NotNull
	protected final F keyFormat;

	@NotNull
	protected final Digits digits;

	protected AbstractCheckDigitNumber(@NotNull final F keyFormat, @NotNull final Digits digits)
	{
		keyFormat.guardCorrectNumberOfDigits(digits);
		keyFormat.guardCheckDigitCorrect(digits);

		this.keyFormat = keyFormat;
		this.digits = digits;
	}

	@NotNull
	@Override
	public final Digit digitAtPositionT(final int oneBasedPositionT)
	{
		return keyFormat.extract(digits, oneBasedPositionT);
	}

	@Override
	@NotNull
	public final Digit digitAt(final int index)
	{
		return digitAtPositionT(index + 1);
	}

	@Override
	@ComparisonResult
	public final int compareTo(@NotNull final DigitList o)
	{
		for (int oneBasedPositionT = T1; oneBasedPositionT <= size(); oneBasedPositionT++)
		{
			final Digit us = digitAtPositionT(oneBasedPositionT);
			final Digit them = o.digitAtPositionT(oneBasedPositionT);
			@ComparisonResult final int digitComparisonResult = us.compareTo(them);
			if (isNotEqualTo(digitComparisonResult))
			{
				return digitComparisonResult;
			}
		}
		return EqualTo;
	}

	@Override
	public final int size()
	{
		return keyFormat.size();
	}

	@Override
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	public final Digit checkDigit()
	{
		return digitAtPositionT(size());
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

		final AbstractCheckDigitNumber that = (AbstractCheckDigitNumber) obj;

		if (!keyFormat.equals(that.keyFormat))
		{
			return false;
		}
		if (!digits.equals(that.digits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = keyFormat.hashCode();
		result = 31 * result + digits.hashCode();
		return result;
	}
}
