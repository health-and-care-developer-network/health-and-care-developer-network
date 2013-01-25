package uk.nhs.hcdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public final class Status extends AbstractToString
{
	private final int statusValue;
	@NotNull @NonNls
	private final String statusText;

	public Status(final int statusValue, @NonNls @NotNull final String statusText)
	{
		this.statusValue = statusValue;
		this.statusText = statusText;
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

		final Status status = (Status) obj;

		if (statusValue != status.statusValue)
		{
			return false;
		}
		if (!statusText.equals(status.statusText))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = statusValue;
		result = 31 * result + statusText.hashCode();
		return result;
	}
}
