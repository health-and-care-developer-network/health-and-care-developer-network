package uk.nhs.hcdn.dts.rats.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.dts.rats.Message;
import uk.nhs.hcdn.dts.rats.response.details.Details;

public final class Response extends AbstractToString
{
	@NotNull
	private final Message message;
	@NotNull
	private final ResponseStatus responseStatus;
	@NotNull
	public final Details details;

	public Response(@NotNull final Message message, @NotNull final ResponseStatus responseStatus, @NotNull final Details details)
	{
		this.message = message;
		this.responseStatus = responseStatus;
		this.details = details;
	}

	public boolean isFor(@NotNull final Message message)
	{
		return this.message.equals(message);
	}

	public boolean hasStatus(@NotNull final ResponseStatus responseStatus)
	{
		return this.responseStatus == responseStatus;
	}

	public boolean hasDetails()
	{
		return details.isKnown();
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

		final Response response = (Response) obj;

		if (!details.equals(response.details))
		{
			return false;
		}
		if (!message.equals(response.message))
		{
			return false;
		}
		if (responseStatus != response.responseStatus)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = message.hashCode();
		result = 31 * result + responseStatus.hashCode();
		result = 31 * result + details.hashCode();
		return result;
	}
}
