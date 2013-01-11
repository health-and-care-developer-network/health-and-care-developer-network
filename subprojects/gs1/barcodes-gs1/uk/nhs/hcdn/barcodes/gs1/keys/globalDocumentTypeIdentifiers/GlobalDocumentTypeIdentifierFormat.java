package uk.nhs.hcdn.barcodes.gs1.keys.globalDocumentTypeIdentifiers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.CheckDigitCalculator;
import uk.nhs.hcdn.barcodes.gs1.keys.SerialComponentKeyFormat;

import java.util.Set;

import static uk.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum GlobalDocumentTypeIdentifierFormat implements SerialComponentKeyFormat
{
	GDTI("Global Document Type Identifier"),
	;
	public static final int MaximumOneBasedPositionT = 13;
	public static final int MaximumSerialDigits = 17;

	@NotNull
	private final String actualName;
	private final Set<String> formerActualNames;
	private final CheckDigitCalculator checkDigitCalculator;

	@SuppressWarnings("MagicNumber")
	GlobalDocumentTypeIdentifierFormat(@NonNls @NotNull final String actualName, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		checkDigitCalculator = new CheckDigitCalculator
		(
			MaximumOneBasedPositionT,
			MaximumSerialDigits
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
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		return checkDigitCalculator.calculateCheckDigit(digits);
	}

	@SuppressWarnings("RefusedBequest")
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

	@Override
	public int size()
	{
		return MaximumOneBasedPositionT;
	}
}
