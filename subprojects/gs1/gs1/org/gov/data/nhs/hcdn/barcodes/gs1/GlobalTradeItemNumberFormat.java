package org.gov.data.nhs.hcdn.barcodes.gs1;

import org.gov.data.nhs.hcdn.barcodes.Digit;
import org.gov.data.nhs.hcdn.common.naming.ActualName;
import org.gov.data.nhs.hcdn.common.naming.FormerActualNames;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static org.gov.data.nhs.hcdn.barcodes.Digit.Zero;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumber.MaximumOneBasedPositionT;
import static org.gov.data.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum GlobalTradeItemNumberFormat implements ActualName, FormerActualNames
{
	GTIN_8("GTIN-8", 8, "EAN-UCC-8", "EAN-8"),
	GTIN_12("GTIN-12", 12, "EAN-UCC-12", "EAN-12", "UPC"),
	GTIN_13("GTIN-13", 13, "EAN-UCC-13", "EAN-13", "CIP"),
	GTIN_14("GTIN-14", MaximumOneBasedPositionT),
	;

	@NotNull
	private final String actualName;
	private final int correctNumberOfDigits;
	private final Set<String> formerActualNames;
	private final int oneBasedPositionTOffset;

	GlobalTradeItemNumberFormat(@NonNls @NotNull final String actualName, final int correctNumberOfDigits, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		this.correctNumberOfDigits = correctNumberOfDigits;
		this.formerActualNames = unmodifiableSetOf(formerActualNames);

		oneBasedPositionTOffset = MaximumOneBasedPositionT - correctNumberOfDigits;
	}

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	public void guardCorrectNumberOfDigits(@NotNull final Digit... digits)
	{
		final int length = digits.length;
		if (correctNumberOfDigits != length)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Incorrect number of digits for GTIN %1$s (expected %2$s digits)", this, length));
		}
	}

	@NotNull
	public Digit extract(@NotNull final Digit[] digits, final int oneBasedPositionT)
	{
		final int index = oneBasedPositionT - 1 - oneBasedPositionTOffset;
		if (index < 0)
		{
			return Zero;
		}
		return digits[index];
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
}
