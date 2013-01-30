package uk.nhs.hdn.dts.rats.response.details;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.SmtpAddress;
import uk.nhs.hdn.dts.domain.Subject;
import uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.ProcessIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.WorkflowIdentifier;
import uk.nhs.hdn.dts.domain.statusRecords.dateTime.DateTime;
import uk.nhs.hdn.dts.domain.statusRecords.dateTime.KnownDateTime;
import uk.nhs.hdn.dts.rats.response.FailureDiagnostic;
import uk.nhs.hdn.dts.rats.response.MessageIdentifier;
import uk.nhs.hdn.dts.rats.response.Status;

import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writePropertyIfKnown;

public final class KnownDetails extends AbstractIsUnknown implements Details
{
	@NotNull
	private final MessageIdentifier messageIdentifier;
	@NotNull
	private final DtsName toDtsName;
	@NotNull
	private final SmtpAddress fromSmtpAddress;
	@NotNull
	private final SmtpAddress toSmtpAddress;
	@NotNull
	private final KnownDateTime transferDateTime;
	@NotNull
	@NonNls
	private final String messageReport;
	@NotNull
	private final Status status;
	@NotNull
	private final DateTime sentDateTime;
	@NotNull
	private final DateTime failureDateTime;
	@NotNull
	@NonNls
	private final FailureDiagnostic failureDiagnostic;
	@NotNull
	private final DtsIdentifier failureMessageDtsIdentifier;
	@NotNull
	private final Subject subject;
	@NotNull
	private final WorkflowIdentifier workflowIdentifier;
	@NotNull
	private final ProcessIdentifier processIdentifier;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public KnownDetails(@NotNull final MessageIdentifier messageIdentifier, @NotNull final DtsName toDtsName, @NotNull final SmtpAddress fromSmtpAddress, @NotNull final SmtpAddress toSmtpAddress, @NotNull final KnownDateTime transferDateTime, @NotNull @NonNls final String messageReport, @NotNull final Status status, @NotNull final DateTime sentDateTime, @NotNull final DateTime failureDateTime, @NotNull @NonNls final FailureDiagnostic failureDiagnostic, @NotNull final DtsIdentifier failureMessageDtsIdentifier, @NotNull final Subject subject, @NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final ProcessIdentifier processIdentifier)
	{
		super(false);
		if (messageIdentifier.isUnknown())
		{
			throw new IllegalArgumentException("MessageIdentifier can not be unknown");
		}
		this.messageIdentifier = messageIdentifier;
		this.toDtsName = toDtsName;
		this.fromSmtpAddress = fromSmtpAddress;
		this.toSmtpAddress = toSmtpAddress;
		this.transferDateTime = transferDateTime;
		this.messageReport = messageReport;
		this.status = status;
		this.sentDateTime = sentDateTime;
		this.failureDateTime = failureDateTime;
		this.failureDiagnostic = failureDiagnostic;
		this.failureMessageDtsIdentifier = failureMessageDtsIdentifier;
		this.subject = subject;
		this.workflowIdentifier = workflowIdentifier;
		this.processIdentifier = processIdentifier;
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("MsgID", messageIdentifier);
			mapSerialiser.writeProperty("ToDTSName", toDtsName);
			mapSerialiser.writeProperty("FromSMTPAddr", fromSmtpAddress);
			mapSerialiser.writeProperty("ToSMTPAddr", toSmtpAddress);
			mapSerialiser.writeProperty("TransferDateTime", transferDateTime);
			mapSerialiser.writeProperty("MsgReport", messageReport);
			mapSerialiser.writeProperty("Status", status);
			writePropertyIfKnown(mapSerialiser, "SentDateTime", sentDateTime);
			writePropertyIfKnown(mapSerialiser, "FailureDateTime", failureDateTime);
			writePropertyIfKnown(mapSerialiser, "FailureDiagnostic", failureDiagnostic);
			writePropertyIfKnown(mapSerialiser, "FailureMsgDTSID", failureMessageDtsIdentifier);
			writePropertyIfKnown(mapSerialiser, "Subject", subject);
			writePropertyIfKnown(mapSerialiser, "WorkflowID", workflowIdentifier);
			writePropertyIfKnown(mapSerialiser, "ProcessID", processIdentifier);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"FeatureEnvy", "OverlyComplexMethod", "OverlyLongMethod"})
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
		if (!super.equals(obj))
		{
			return false;
		}

		final KnownDetails that = (KnownDetails) obj;

		if (!failureDateTime.equals(that.failureDateTime))
		{
			return false;
		}
		if (!failureDiagnostic.equals(that.failureDiagnostic))
		{
			return false;
		}
		if (!failureMessageDtsIdentifier.equals(that.failureMessageDtsIdentifier))
		{
			return false;
		}
		if (!fromSmtpAddress.equals(that.fromSmtpAddress))
		{
			return false;
		}
		if (!messageIdentifier.equals(that.messageIdentifier))
		{
			return false;
		}
		if (!messageReport.equals(that.messageReport))
		{
			return false;
		}
		if (!processIdentifier.equals(that.processIdentifier))
		{
			return false;
		}
		if (!sentDateTime.equals(that.sentDateTime))
		{
			return false;
		}
		if (!status.equals(that.status))
		{
			return false;
		}
		if (!subject.equals(that.subject))
		{
			return false;
		}
		if (!toDtsName.equals(that.toDtsName))
		{
			return false;
		}
		if (!toSmtpAddress.equals(that.toSmtpAddress))
		{
			return false;
		}
		if (!transferDateTime.equals(that.transferDateTime))
		{
			return false;
		}
		if (!workflowIdentifier.equals(that.workflowIdentifier))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + messageIdentifier.hashCode();
		result = 31 * result + toDtsName.hashCode();
		result = 31 * result + fromSmtpAddress.hashCode();
		result = 31 * result + toSmtpAddress.hashCode();
		result = 31 * result + transferDateTime.hashCode();
		result = 31 * result + messageReport.hashCode();
		result = 31 * result + status.hashCode();
		result = 31 * result + sentDateTime.hashCode();
		result = 31 * result + failureDateTime.hashCode();
		result = 31 * result + failureDiagnostic.hashCode();
		result = 31 * result + failureMessageDtsIdentifier.hashCode();
		result = 31 * result + subject.hashCode();
		result = 31 * result + workflowIdentifier.hashCode();
		result = 31 * result + processIdentifier.hashCode();
		return result;
	}
}
