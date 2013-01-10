package uk.nhs.hcdn.barcodes.gs1.serialShippingContainerCodes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.CheckDigitCalculator;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.ExtractingCheckDigit;
import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.FormerActualNames;

import java.util.Set;

import static uk.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum SerialShippingContainerCodeFormat implements ActualName, FormerActualNames, ExtractingCheckDigit
{
	SSCS("Serial Shipping Container Code"),
	;
	public static final int T18 = 14;
	public static final int MaximumOneBasedPositionT = T18;

	@NotNull
	private final String actualName;
	private final Set<String> formerActualNames;
	private final CheckDigitCalculator checkDigitCalculator;

	@SuppressWarnings("MagicNumber")
	SerialShippingContainerCodeFormat(@NonNls @NotNull final String actualName, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		final int correctNumberOfDigits = 18;
		checkDigitCalculator = new CheckDigitCalculator
		(
			correctNumberOfDigits,
			0,
			3,
			1,
			3,
			1,
			3,
			1,
			3,
			1,
			3,
			1,
			3,
			1,
			3,
			1,
			3,
			1,
			3
		);
		this.formerActualNames = unmodifiableSetOf(formerActualNames);
	}

	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@Override
	public void guardCorrectNumberOfDigitsIfNoCheckDigit(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCorrectNumberOfDigitsIfNoCheckDigit(digits);
	}

	@Override
	public void guardCorrectNumberOfDigits(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCorrectNumberOfDigits(digits);
	}

	@Override
	public void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCheckDigitCorrect(digits);
	}

	@Override
	@NotNull
	public Digit calculateCheckDigit(@NotNull final Digits withoutCheckDigit)
	{
		return checkDigitCalculator.calculateCheckDigit(withoutCheckDigit);
	}

	@Override
	@NotNull
	public String toString()
	{
		return actualName();
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return formerActualNames;
	}

	@Override
	@NotNull
	public Digit extract(@NotNull final DigitList digits, final int oneBasedPositionT)
	{
		return digits.digitAtPositionT(oneBasedPositionT);
	}
}
