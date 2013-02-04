package uk.nhs.hdn.number;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.*;
import uk.nhs.hdn.common.exceptions.ImpossibleEnumeratedStateException;

import static uk.nhs.hdn.common.digits.Digit.digit;

public final class NhsNumberExtractingCheckDigitCalculator extends AbstractExtractingCheckDigitCalculator
{
	private static final int Modulo11 = 11;

	@NotNull
	public static final NhsNumberExtractingCheckDigitCalculator NhsNumberExtractingCheckDigitCalculatorInstance = new NhsNumberExtractingCheckDigitCalculator();

	private NhsNumberExtractingCheckDigitCalculator()
	{
		super(10);
	}

	@Override
	public void guardCorrectNumberOfDigits(@NotNull final Digits digits)
	{
		if (digits.hasSize(correctNumberOfDigits))
		{
			return;
		}
		throw new IncorrectNumberOfDigitsIllegalStateException(correctNumberOfDigits, correctNumberOfDigits);
	}

	@NotNull
	@Override
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		int sum = 0;
		for(int index = 1; index <= 9; index++)
		{
			final int scalar = Modulo11 - index;
			final int scaledDigit = scalar * digits.digitAtPositionT(index).digit();
			sum += scaledDigit;
		}

		final int remainder = sum % Modulo11;
		final int prototypeCheckDigit = Modulo11 - remainder;
		return digit(validateDigit(prototypeCheckDigit, digits));
	}

	@SuppressWarnings("SwitchStatementWithTooManyBranches")
	private static int validateDigit(final int prototypeCheckDigit, @NotNull final Digits digits)
	{
		switch (prototypeCheckDigit)
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				return prototypeCheckDigit;

			case 10:
				throw new IncorrectNumberCouldNotCalculateCheckDigitIllegalStateException(digits);

			case Modulo11:
				return 0;

			default:
				throw new ImpossibleEnumeratedStateException();
		}

	}
}
