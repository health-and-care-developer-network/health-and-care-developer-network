package org.gov.data.nhs.hcdn.barcodes.gs1;

import org.gov.data.nhs.hcdn.barcodes.Digit;
import org.gov.data.nhs.hcdn.barcodes.gs1.restrictedCirculation.RestrictedCirculationNumber;
import org.gov.data.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;

import static org.gov.data.nhs.hcdn.common.VariableArgumentsHelper.copyOf;

public final class Gs1CompanyPrefixAndItem extends AbstractToString
{
	private static final int NumberOfDigits = 12;

	@NotNull
	private final Digit[] digits;

	public Gs1CompanyPrefixAndItem(@NotNull final Digit... digits)
	{
		if (digits.length != NumberOfDigits)
		{
			throw new IllegalArgumentException("GS1 Company Prefix and Item must be 12 digits");
		}
		this.digits = copyOf(digits);
	}

	@NotNull
	public RestrictedCirculationNumber restrictedCirculationNumber()
	{
		return new RestrictedCirculationNumber(digits[0], digits[1], digits[2]);
	}
}
