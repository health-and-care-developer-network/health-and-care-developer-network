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

package uk.nhs.hdn.common.digits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import static uk.nhs.hdn.common.IntegerHelper.isEven;

public abstract class AbstractExtractingCheckDigitCalculator extends AbstractToString implements ExtractingCheckDigitCalculator
{
	protected final int correctNumberOfDigits;
	@ExcludeFromToString
	protected final int correctNumberOfDigitsLessCheckDigit;
	@ExcludeFromToString protected final boolean evenNumberOfDigits;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	protected AbstractExtractingCheckDigitCalculator(final int correctNumberOfDigits)
	{
		this.correctNumberOfDigits = correctNumberOfDigits;
		correctNumberOfDigitsLessCheckDigit = correctNumberOfDigits - 1;
		evenNumberOfDigits = isEven(correctNumberOfDigits);
	}

	@Override
	public final void guardCorrectNumberOfDigitsIfNoCheckDigit(@NotNull final Digits digits)
	{
		if (digits.hasSize(correctNumberOfDigitsLessCheckDigit))
		{
			return;
		}
		throw new IncorrectNumberOfDigitsWithoutCheckDigitIllegalStateException(correctNumberOfDigitsLessCheckDigit);
	}

	@Override
	public final int size()
	{
		return correctNumberOfDigits;
	}

	@NotNull
	@Override
	public final Digit extract(@NotNull final DigitList digits, final int oneBasedPositionT)
	{
		return digits.digitAtPositionT(oneBasedPositionT);
	}

	@Override
	public final void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		final Digit checkDigit = calculateCheckDigit(digits);
		if (checkDigit != digits.digitAt(correctNumberOfDigitsLessCheckDigit))
		{
			throw new IncorrectCheckDigitIllegalStateException(digits, checkDigit);
		}
	}

	@Override
	@NotNull
	public final Digits addCheckDigit(@NotNull final Digits withoutCheckDigits)
	{
		final Digit checkDigit = calculateCheckDigit(withoutCheckDigits);
		return withoutCheckDigits.add(checkDigit);
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

		final AbstractExtractingCheckDigitCalculator that = (AbstractExtractingCheckDigitCalculator) obj;

		if (correctNumberOfDigits != that.correctNumberOfDigits)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return correctNumberOfDigits;
	}
}
