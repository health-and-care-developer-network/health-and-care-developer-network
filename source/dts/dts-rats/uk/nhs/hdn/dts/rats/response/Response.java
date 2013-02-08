package uk.nhs.hdn.dts.rats.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.dts.rats.Message;
import uk.nhs.hdn.dts.rats.response.details.Details;

import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writePropertyIfKnown;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.recurse;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class Response extends AbstractToString implements MapSerialisable
{
	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForResponse()
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForResponse()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		"MessageFromDtsName",
		"MessageLocalIdentifier",
		"ResponseStatus",
		"DetailsMessageIdentifier",
		"DetailsToDtsName",
		"DetailsFromSmtpAddress",
		"DetailsToSmtpAddress",
		"DetailsTransferDateTime",
		"DetailsMessageReport",
		"DetailsStatusValue",
		"DetailsStatusText",
		"DetailsSentDateTime",
		"DetailsFailureDateTime",
		"DetailsFailureDiagnostic",
		"DetailsFailureMessageDtsIdentifier",
		"DetailsSubject",
		"DetailsWorkflowIdentifier",
		"DetailsProcessIdentifier"
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		recurse("Message",
			leaf("FromDTSName", 0),
			leaf("LocalID", 1),
		leaf("ResponseStatus", 2),
		recurse("Details",
			leaf("MsgID", 3),
			leaf("ToDTSName", 4),
			leaf("FromSMTPAddr", 5),
			leaf("ToSMTPAddr", 6),
			leaf("TransferDateTime", 7),
			leaf("MsgReport", 8),
			recurse("Status",
				leaf("StatusVal", 9),
				leaf("StatusText", 10)
			),
			leaf("SentDateTime", 11),
			leaf("FailureDateTime", 12),
			leaf("FailureDiagnostic", 13),
			leaf("FailureMsgDTSID", 14),
			leaf("Subject", 15),
			leaf("WorkflowID", 16),
			leaf("ProcessID", 17)
		)
	));


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

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("Message", message);
			mapSerialiser.writeProperty("ResponseStatus", responseStatus);
			writePropertyIfKnown(mapSerialiser, "Details", details);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
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
