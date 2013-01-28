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

package uk.nhs.hdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.Digit;
import uk.nhs.hdn.barcodes.DigitList;
import uk.nhs.hdn.barcodes.Digits;
import uk.nhs.hdn.common.comparison.ComparisonResult;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.T1;
import static uk.nhs.hdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hdn.common.comparison.ComparisonResult.EqualTo;

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
