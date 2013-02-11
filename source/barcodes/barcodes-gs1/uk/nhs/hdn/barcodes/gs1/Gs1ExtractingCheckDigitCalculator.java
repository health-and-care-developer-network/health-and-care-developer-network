/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.barcodes.gs1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.digits.AbstractExtractingCheckDigitCalculator;
import uk.nhs.hdn.common.digits.AbstractExtractingCheckDigitCalculator;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.common.digits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import static uk.nhs.hdn.common.digits.Digit.Zero;
import static uk.nhs.hdn.common.digits.Digit.digit;

public final class Gs1ExtractingCheckDigitCalculator extends AbstractExtractingCheckDigitCalculator
{
	private final int maximumSizeOfSerialNumber;
	private final int offset;
	@ExcludeFromToString private final int correctNumberOfDigitsIncludingSerialComponent;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public Gs1ExtractingCheckDigitCalculator(final int correctNumberOfDigits)
	{
		this(correctNumberOfDigits, 0);
	}

	public Gs1ExtractingCheckDigitCalculator(final int correctNumberOfDigits, final int maximumSizeOfSerialNumber)
	{
		this(correctNumberOfDigits, maximumSizeOfSerialNumber, 0);
	}

	public Gs1ExtractingCheckDigitCalculator(final int correctNumberOfDigits, final int maximumSizeOfSerialNumber, final int offset)
	{
		super(correctNumberOfDigits);
		this.maximumSizeOfSerialNumber = maximumSizeOfSerialNumber;
		this.offset = offset;
		correctNumberOfDigitsIncludingSerialComponent = correctNumberOfDigits + maximumSizeOfSerialNumber;
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
		if (!super.equals(obj))
		{
			return false;
		}

		final Gs1ExtractingCheckDigitCalculator that = (Gs1ExtractingCheckDigitCalculator) obj;

		if (maximumSizeOfSerialNumber != that.maximumSizeOfSerialNumber)
		{
			return false;
		}
		if (offset != that.offset)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + maximumSizeOfSerialNumber;
		result = 31 * result + offset;
		return result;
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
