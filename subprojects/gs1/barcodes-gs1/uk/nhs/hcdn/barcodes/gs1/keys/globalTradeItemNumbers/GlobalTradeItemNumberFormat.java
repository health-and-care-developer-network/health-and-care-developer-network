package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.CheckDigitCalculator;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.ExtractingCheckDigit;
import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.FormerActualNames;

import java.util.Set;

import static uk.nhs.hcdn.barcodes.Digit.Zero;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum GlobalTradeItemNumberFormat implements ActualName, FormerActualNames, ExtractingCheckDigit
{
	GTIN_8("GTIN-8", 8, "EAN-UCC-8", "EAN-8"),
	// 11 digits, 1 trailing check digit
	GTIN_12("GTIN-12", 12, "EAN-UCC-12", "EAN-12", "UPC"),
	GTIN_13("GTIN-13", 13, "EAN-UCC-13", "EAN-13", "CIP"),
	GTIN_14("GTIN-14", 14),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final GlobalTradeItemNumberFormat[] Index = new GlobalTradeItemNumberFormat[14 + 1];
	}

	public static final int T1 = 1;
	public static final int T2 = 2;
	public static final int T13 = 13;
	public static final int T14 = 14;
	public static final int MaximumOneBasedPositionT = T14;

	@NotNull
	private final String actualName;
	private final Set<String> formerActualNames;
	private final int oneBasedPositionTOffset;
	private final CheckDigitCalculator checkDigitCalculator;

	GlobalTradeItemNumberFormat(@NonNls @NotNull final String actualName, final int correctNumberOfDigits, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		this.formerActualNames = unmodifiableSetOf(formerActualNames);

		oneBasedPositionTOffset = MaximumOneBasedPositionT - correctNumberOfDigits;

		CompilerWorkaround.Index[correctNumberOfDigits] = this;
		checkDigitCalculator = new CheckDigitCalculator
		(
			correctNumberOfDigits,
			oneBasedPositionTOffset,
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
	}

	@NonNls
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

	@NotNull
	public Digit extract(@NotNull final DigitList digits, final int oneBasedPositionT)
	{
		final int index = oneBasedPositionT - oneBasedPositionTOffset;
		if (index < T1)
		{
			return Zero;
		}
		return digits.digitAtPositionT(index);
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return formerActualNames;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return actualName;
	}

	@Nullable
	public static GlobalTradeItemNumberFormat globalTradeItemNumberFormatNumberOfDigits(final int numberOfDigits)
	{
		if (numberOfDigits < CompilerWorkaround.Index.length)
		{
			return CompilerWorkaround.Index[numberOfDigits];
		}
		return null;
	}
}
