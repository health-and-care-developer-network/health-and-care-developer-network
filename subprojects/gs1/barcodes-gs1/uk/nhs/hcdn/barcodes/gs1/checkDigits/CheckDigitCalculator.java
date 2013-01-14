/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import static uk.nhs.hcdn.barcodes.Digit.Zero;
import static uk.nhs.hcdn.barcodes.Digit.digit;
import static uk.nhs.hcdn.common.IntegerHelper.isEven;

public final class CheckDigitCalculator extends AbstractToString implements CheckDigit
{
	private final int correctNumberOfDigits;
	private final int maximumSizeOfSerialNumber;
	private final int offset;
	@ExcludeFromToString private final int correctNumberOfDigitsLessCheckDigit;
	@ExcludeFromToString private final int correctNumberOfDigitsIncludingSerialComponent;
	@ExcludeFromToString private final boolean evenNumberOfDigits;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public CheckDigitCalculator(final int correctNumberOfDigits)
	{
		this(correctNumberOfDigits, 0);
	}

	public CheckDigitCalculator(final int correctNumberOfDigits, final int maximumSizeOfSerialNumber)
	{
		this(correctNumberOfDigits, maximumSizeOfSerialNumber, 0);
	}

	public CheckDigitCalculator(final int correctNumberOfDigits, final int maximumSizeOfSerialNumber, final int offset)
	{
		this.correctNumberOfDigits = correctNumberOfDigits;
		this.maximumSizeOfSerialNumber = maximumSizeOfSerialNumber;
		this.offset = offset;
		correctNumberOfDigitsLessCheckDigit = correctNumberOfDigits - 1;
		correctNumberOfDigitsIncludingSerialComponent = correctNumberOfDigits + maximumSizeOfSerialNumber;
		evenNumberOfDigits = isEven(correctNumberOfDigits);
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

		final CheckDigitCalculator that = (CheckDigitCalculator) obj;

		if (correctNumberOfDigits != that.correctNumberOfDigits)
		{
			return false;
		}
		if (correctNumberOfDigitsLessCheckDigit != that.correctNumberOfDigitsLessCheckDigit)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = correctNumberOfDigits;
		result = 31 * result + correctNumberOfDigitsLessCheckDigit;
		return result;
	}

	@Override
	public void guardCorrectNumberOfDigitsIfNoCheckDigit(@NotNull final Digits digits)
	{
		if (digits.hasSize(correctNumberOfDigitsLessCheckDigit))
		{
			return;
		}
		throw new IncorrectNumberOfDigitsWithoutCheckDigitIllegalStateException(correctNumberOfDigitsLessCheckDigit);
	}

	@Override
	public void guardCorrectNumberOfDigits(@NotNull final Digits digits)
	{
		if (digits.hasSizeBetween(correctNumberOfDigits, correctNumberOfDigitsIncludingSerialComponent))
		{
			return;
		}
		throw new IncorrectNumberOfDigitsIllegalStateException(correctNumberOfDigits, maximumSizeOfSerialNumber);
	}

	@Override
	public void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		final Digit checkDigit = calculateCheckDigit(digits);
		if (checkDigit != digits.digitAt(correctNumberOfDigitsLessCheckDigit))
		{
			throw new IncorrectCheckDigitIllegalStateException(digits, checkDigit);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	@NotNull
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		int oddPositionSum = 0;
		for(int index = offset; index < correctNumberOfDigitsLessCheckDigit - offset; index += 2)
		{
			oddPositionSum += digits.digitAt(index).digit();
		}

		int evenPositionSum = 0;
		for(int index = offset + 1; index < correctNumberOfDigitsLessCheckDigit - offset; index += 2)
		{
			evenPositionSum += digits.digitAt(index).digit();
		}

		final int sum = evenNumberOfDigits ? oddPositionSum * 3 + evenPositionSum : oddPositionSum + evenPositionSum * 3;
		final int multipleOfTen = sum / 10;
		final int remainder = sum - (10 * multipleOfTen);
		final boolean hasRemainder = remainder != 0;
		return hasRemainder ? digit(10 - remainder) : Zero;
	}
}
