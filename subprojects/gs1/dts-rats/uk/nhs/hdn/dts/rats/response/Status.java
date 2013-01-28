package uk.nhs.hdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;

public final class Status extends AbstractToString implements MapSerialisable
{
	@NotNull
	public final StatusValue statusValue;
	@NotNull @NonNls
	public final String statusText;

	public Status(@NotNull final StatusValue statusValue, @NonNls @NotNull final String statusText)
	{
		this.statusValue = statusValue;
		this.statusText = statusText;
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("StatusVal", statusValue);
			mapSerialiser.writeProperty("StatusText", statusText);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
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

		if (!statusText.equals(status.statusText))
		{
			return false;
		}
		if (statusValue != status.statusValue)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = statusValue.hashCode();
		result = 31 * result + statusText.hashCode();
		return result;
	}
}
