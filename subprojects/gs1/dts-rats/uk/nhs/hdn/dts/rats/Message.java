package uk.nhs.hdn.dts.rats;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;

public final class Message extends AbstractToString implements MapSerialisable
{
	@NotNull
	public final DtsName fromDtsName;
	@NotNull
	public final LocalIdentifier localIdentifier;

	public Message(@NotNull final DtsName fromDtsName, @NotNull final LocalIdentifier localIdentifier)
	{
		this.fromDtsName = fromDtsName;
		this.localIdentifier = localIdentifier;
		if (fromDtsName.isUnknown())
		{
			throw new IllegalArgumentException("fromDtsName can not be unknown");
		}
		if (localIdentifier.isUnknown())
		{
			throw new IllegalArgumentException("localIdentifier can not be unknown");
		}
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("FromDTSName", fromDtsName);
			mapSerialiser.writeProperty("LocalID", localIdentifier);
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

		final Message message = (Message) obj;

		if (!fromDtsName.equals(message.fromDtsName))
		{
			return false;
		}
		if (!localIdentifier.equals(message.localIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = fromDtsName.hashCode();
		result = 31 * result + localIdentifier.hashCode();
		return result;
	}
}
