package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import static uk.nhs.hcdn.barcodes.Digit.Zero;
import static uk.nhs.hcdn.barcodes.Digit.digit;

public final class CheckDigitCalculator extends AbstractToString implements CheckDigit
{
	private final int correctNumberOfDigits;
	private final int oneBasedPositionTOffset;
	private final int[] checkDigitScalarMatrix;
	@ExcludeFromToString private final int correctNumberOfDigitsLessCheckDigit;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public CheckDigitCalculator(final int correctNumberOfDigits, final int oneBasedPositionTOffset, final int... checkDigitScalarMatrix)
	{
		this.correctNumberOfDigits = correctNumberOfDigits;
		this.oneBasedPositionTOffset = oneBasedPositionTOffset;
		this.checkDigitScalarMatrix = checkDigitScalarMatrix;
		correctNumberOfDigitsLessCheckDigit = correctNumberOfDigits - 1;
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
		if (digits.hasSize(correctNumberOfDigits))
		{
			return;
		}
		throw new IncorrectNumberOfDigitsIllegalStateException(correctNumberOfDigits);
	}

	@Override
	public void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		final Digit checkDigit = calculateCheckDigit(digits, digits.size() - 1);
		if (checkDigit != digits.digitAt(correctNumberOfDigitsLessCheckDigit))
		{
			throw new IncorrectCheckDigitIllegalStateException(digits, checkDigit);
		}
	}

	@Override
	@NotNull
	public Digit calculateCheckDigit(@NotNull final Digits withoutCheckDigit)
	{
		guardCorrectNumberOfDigitsIfNoCheckDigit(withoutCheckDigit);
		final int size = withoutCheckDigit.size();

		return calculateCheckDigit(withoutCheckDigit, size);
	}

	private Digit calculateCheckDigit(final DigitList digits, final int size)
	{
		int sum = 0;
		for (int index = 0; index < size; index++)
		{
			sum += checkDigitScalarMatrix[index + oneBasedPositionTOffset] * digits.digitAt(index).digit();
		}
		final int multipleOfTen = sum / 10;
		final int remainder = sum - (10 * multipleOfTen);
		final boolean hasRemainder = remainder != 0;
		return hasRemainder ? digit(10 - remainder) : Zero;
	}
}
