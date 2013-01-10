package uk.nhs.hcdn.barcodes.gs1;

import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.restrictedCirculation.RestrictedCirculationNumber;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

public final class Gs1CompanyPrefixAndItem extends AbstractToString
{
	public static final int NumberOfDigits = 12;

	@NotNull
	private final Digits digits;

	public Gs1CompanyPrefixAndItem(@NotNull final Digits digits)
	{
		this.digits = digits;
	}

	@NotNull
	public RestrictedCirculationNumber restrictedCirculationNumber()
	{
		return new RestrictedCirculationNumber(digits.firstThree());
	}
}
