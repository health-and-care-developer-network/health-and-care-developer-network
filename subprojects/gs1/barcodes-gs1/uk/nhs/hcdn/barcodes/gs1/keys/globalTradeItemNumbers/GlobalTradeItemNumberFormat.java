package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.CheckDigitCalculator;
import uk.nhs.hcdn.barcodes.gs1.keys.KeyFormat;
import uk.nhs.hcdn.common.exceptions.ImpossibleEnumeratedStateException;

import java.util.Set;

import static uk.nhs.hcdn.barcodes.Digit.*;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum GlobalTradeItemNumberFormat implements KeyFormat
{
	// ???
	GTIN_8("GTIN-8", 8, "EAN-UCC-8", "EAN-8")
	{
		@NotNull
		@Override
		public Digits gs1PrefixDigits(@NotNull final Digits digits)
		{
			throw new UnsupportedOperationException("Not implemented due to time pressures");
		}
	},
	// 2 digit prefix*, 9 digits company prefix + item, 1 check digit
	// From the old US GS1 MO range, so prefix with a zero
	GTIN_12("GTIN-12", 12, "EAN-UCC-12", "EAN-12", "UPC")
	{
		@NotNull
		@Override
		public Digits gs1PrefixDigits(@NotNull final Digits digits)
		{
			return new Digits(false, Zero).add(digits.slice(1, 2));
		}
	},
	// 3 digit prefix, 3-8 digits company prefix, 6 - 1 item, 1 check digit
	GTIN_13("GTIN-13", 13, "EAN-UCC-13", "EAN-13", "CIP")
	{
		@NotNull
		@Override
		public Digits gs1PrefixDigits(@NotNull final Digits digits)
		{
			return digits.slice(1, 3);
		}
	},
	// 1 indicator digit, 3 digit prefix, 3-8 digits company prefix, 6 - 1 item, 1 check digit
	GTIN_14("GTIN-14", 14)
	{
		@NotNull
		@Override
		public Digits gs1PrefixDigits(@NotNull final Digits digits)
		{
			return digits.slice(2, 4);
		}
	},
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
			correctNumberOfDigits
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
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		return checkDigitCalculator.calculateCheckDigit(digits);
	}

	@Override
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

	@Override
	public int size()
	{
		return MaximumOneBasedPositionT;
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

	@NotNull
	public abstract Digits gs1PrefixDigits(@NotNull final Digits digits);

	@NotNull
	public static Digits upcEToUpcA(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Digits eightDigits)
	{
		if (eightDigits.size() != 8)
		{
			throw new IllegalArgumentException("digits must have a size of 8");
		}

		final Digit digit1 = eightDigits.digitAt(1);
		final Digit digit2 = eightDigits.digitAt(2);
		final Digit digit3 = eightDigits.digitAt(3);
		final Digit digit4 = eightDigits.digitAt(4);
		final Digit digit5 = eightDigits.digitAt(5);
		final Digit digit6 = eightDigits.digitAt(6);

		final Digits manufacturerNumber;
		final Digits itemNumber;
		switch (digit6)
		{
			case Zero:
				manufacturerNumber = new Digits(false, digit1, digit2, digit6, Zero, Zero);
				itemNumber = new Digits(false, Zero, Zero, digit3, digit4, digit5);
				break;

			case One:
				manufacturerNumber = new Digits(false, digit1, digit2, digit6, Zero, Zero);
				itemNumber = new Digits(false, Zero, Zero, digit3, digit4, digit5);
				break;

			case Two:
				manufacturerNumber = new Digits(false, digit1, digit2, digit6, Zero, Zero);
				itemNumber = new Digits(false, Zero, Zero, digit3, digit4, digit5);
				break;

			case Three:
				manufacturerNumber = new Digits(false, digit1, digit2, digit3, Zero, Zero);
				itemNumber = new Digits(false, Zero, Zero, Zero, digit4, digit5);
				break;

			case Four:
				manufacturerNumber = new Digits(false, digit1, digit2, digit3, digit4, Zero);
				itemNumber = new Digits(false, Zero, Zero, Zero, Zero, digit5);
				break;

			case Five:
			case Six:
			case Seven:
			case Eight:
			case Nine:
				manufacturerNumber = new Digits(false, digit1, digit2, digit3, digit4, digit5);
				itemNumber = new Digits(false, Zero, Zero, Zero, Zero, digit6);
				break;

			default:
				throw new ImpossibleEnumeratedStateException();
		}

		final Digits withoutCheckDigit = new Digits(false, Zero).add(manufacturerNumber).add(itemNumber);
		final Digit checkDigit = GTIN_12.calculateCheckDigit(withoutCheckDigit);
		return withoutCheckDigit.add(checkDigit);
	}

	public static Digits upcAToUpcE(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Digits twelveDigits) throws DigitsCanNotbeCompressedException
	{
		final Digit first = twelveDigits.digitAt(0);
		if (!first.isZeroToOne())
		{
			throw new DigitsCanNotbeCompressedException("as they do not start with zero or one");
		}

		final Digits withoutCheckDigit = new Digits(false, first).add(extract(twelveDigits));
		final Digit checkDigit = GTIN_8.calculateCheckDigit(withoutCheckDigit);
		return withoutCheckDigit.add(checkDigit);
	}

	private static Digits extract(final Digits twelveDigits) throws DigitsCanNotbeCompressedException
	{
		final Digit thirdDigit = twelveDigits.digitAt(3);
		final Digit fourthDigit = twelveDigits.digitAt(4);
		final Digit fifthDigit = twelveDigits.digitAt(5);
		if (thirdDigit.isZeroToTwo() && fourthDigit == Zero && fifthDigit == Zero)
		{
			return twelveDigits.slice(1, 3).add(twelveDigits.slice(8, 11)).add(twelveDigits.slice(3, 4));
		}
		if (fourthDigit == Zero && fifthDigit == Zero)
		{
			return twelveDigits.slice(1, 4).add(twelveDigits.slice(9, 11)).add(Three);
		}
		if (fifthDigit == Zero)
		{
			return twelveDigits.slice(1, 5).add(twelveDigits.digitAt(10)).add(Four);
		}
		if (twelveDigits.digitAt(10).isGreaterThanOrEqualTo(Five))
		{
			return twelveDigits.slice(1, 6).add(twelveDigits.digitAt(10));
		}
		throw new DigitsCanNotbeCompressedException("because the product coder is not between 00005 and 00009");
	}
}
