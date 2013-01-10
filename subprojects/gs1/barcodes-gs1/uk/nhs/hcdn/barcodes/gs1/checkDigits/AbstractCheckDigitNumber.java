package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers.GlobalTradeItemNumberFormat.T1;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;

public abstract class AbstractCheckDigitNumber extends AbstractToString implements CheckDigitNumber
{
	@NotNull
	private final ExtractingCheckDigit extractingDigitCalculator;

	@NotNull
	private final Digits digits;

	protected AbstractCheckDigitNumber(@NotNull final ExtractingCheckDigit extractingCheckDigit, @NotNull final Digits digits)
	{
		extractingCheckDigit.guardCorrectNumberOfDigits(digits);
		extractingCheckDigit.guardCheckDigitCorrect(digits);

		extractingDigitCalculator = extractingCheckDigit;
		this.digits = digits;
	}

	@NotNull
	@Override
	public final Digit digitAtPositionT(final int oneBasedPositionT)
	{
		return extractingDigitCalculator.extract(digits, oneBasedPositionT);
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

		if (!extractingDigitCalculator.equals(that.extractingDigitCalculator))
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
		int result = extractingDigitCalculator.hashCode();
		result = 31 * result + digits.hashCode();
		return result;
	}
}
