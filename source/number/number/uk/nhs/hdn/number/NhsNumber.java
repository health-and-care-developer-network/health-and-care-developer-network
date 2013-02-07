package uk.nhs.hdn.number;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.AbstractCheckDigitNumber;
import uk.nhs.hdn.common.digits.Digits;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.digits.Digits.digits;
import static uk.nhs.hdn.number.NhsNumberExtractingCheckDigitCalculator.NhsNumberExtractingCheckDigitCalculatorInstance;

public final class NhsNumber extends AbstractCheckDigitNumber<NhsNumberExtractingCheckDigitCalculator>
{

	private static final int NhsNumberHasSeparators = 12;

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public static NhsNumber valueOf(@NotNull final CharSequence digits)
	{
		final Digits parsedDigits;

		final int length = digits.length();
		if (length == 10)
		{
			parsedDigits = digits(digits);
		}
		else if (length == NhsNumberHasSeparators)
		{
			parsedDigits = digits(digits.subSequence(0, 3)).add(digits(digits.subSequence(4, 7))).add(digits(digits.subSequence(8, NhsNumberHasSeparators)));
		}
		else
		{
			throw new IllegalArgumentException(format(ENGLISH, "%1$s is neither 10 or 12 characters long", digits.toString()));
		}

		return new NhsNumber(parsedDigits);
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

	@SuppressWarnings("MagicCharacter")
	@NotNull
	public String formattedForDisplay()
	{
		return formattedForDisplay(' ');
	}

	@NotNull
	public String formattedForDisplay(final char separator)
	{
		return new String(new char[]
		{
			charAt(1),
			charAt(2),
			charAt(3),
			separator,
			charAt(4),
			charAt(5),
			charAt(6),
			separator,
			charAt(7),
			charAt(8),
			charAt(9),
			charAt(10)
		});
	}

	private char charAt(final int oneBasedPosition)
	{
		return digitAtPositionT(oneBasedPosition).toChar();
	}
}
