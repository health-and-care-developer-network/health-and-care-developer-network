package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hcdn.barcodes.Digit.Zero;

public final class AbstractCompanyPrefix extends AbstractToString
{
	@NotNull
	private final Digits digits;

	public AbstractCompanyPrefix(final @NotNull Digits digits)
	{
		this.digits = digits;
	}

	@Override
	public boolean equals(final @Nullable Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractCompanyPrefix that = (AbstractCompanyPrefix) obj;

		if (!digits.equals(that.digits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return digits.hashCode();
	}

	@NotNull
	public Gs1CompanyPrefix toGs1CompanyPrefix()
	{
		return new Gs1CompanyPrefix(new Digits(false, Zero).add(digits));
	}
}
