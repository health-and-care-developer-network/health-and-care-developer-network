package uk.nhs.hdn.number;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.AbstractCheckDigitNumber;
import uk.nhs.hdn.common.digits.Digits;

import static uk.nhs.hdn.number.NhsNumberExtractingCheckDigitCalculator.NhsNumberExtractingCheckDigitCalculatorInstance;

public final class NhsNumber extends AbstractCheckDigitNumber<NhsNumberExtractingCheckDigitCalculator>
{
	@NotNull
	public static NhsNumber valueOf(@NotNull final CharSequence digits)
	{
		return new NhsNumber(Digits.digits(digits));
	}

	public NhsNumber(@NotNull final Digits digits)
	{
		super(NhsNumberExtractingCheckDigitCalculatorInstance, digits);
	}

	@NotNull
	public CharSequence toDigitsString()
	{
		return digits.toString();
	}
}
