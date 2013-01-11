package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public abstract class AbstractCompanyPrefix extends AbstractToString
{
	@NotNull
	protected final Digits digits;

	protected AbstractCompanyPrefix(@NotNull final Digits digits)
	{
		this.digits = digits;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
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
}
