/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.dts.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.unknown.IsUnknown;
import uk.nhs.hdn.dts.domain.identifiers.*;
import uk.nhs.hdn.dts.domain.statusRecords.StatusRecord;

import static uk.nhs.hdn.dts.domain.AddressPair.UnknownDtsNames;
import static uk.nhs.hdn.dts.domain.AddressPair.UnknownSmtpAddresses;
import static uk.nhs.hdn.dts.domain.AddressType.determineAddressType;
import static uk.nhs.hdn.dts.domain.BooleanFlag.UnknownBooleanFlag;
import static uk.nhs.hdn.dts.domain.DataChecksum.UnknownDataChecksum;
import static uk.nhs.hdn.dts.domain.MessageType.Data;
import static uk.nhs.hdn.dts.domain.Version.VersionMajor1Minor0;
import static uk.nhs.hdn.dts.domain.identifiers.PartnerIdentifier.UnknownPartnerIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.ProcessIdentifier.UnknownProcessIdentifier;
import static uk.nhs.hdn.dts.domain.statusRecords.UnknownStatusRecord.UnknownStatusRecordInstance;

public final class ControlFile extends AbstractToString implements MapSerialisable, Serialisable
{
	@NotNull
	public static ControlFile dataControlFileForSmtpNames(@NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final ContentEncoding contentEncoding, @NotNull final AddressPair<SmtpAddress> smtpAddresses)
	{
		return dataControlFile(workflowIdentifier, subject, localIdentifier, dtsIdentifier, contentEncoding, smtpAddresses, UnknownDtsNames);
	}

	@NotNull
	public static ControlFile dataControlFileForDtsNames(@NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final ContentEncoding contentEncoding, @NotNull final AddressPair<DtsName> dtsNames)
	{
		return dataControlFile(workflowIdentifier, subject, localIdentifier, dtsIdentifier, contentEncoding, UnknownSmtpAddresses, dtsNames);
	}

	@NotNull
	public static ControlFile dataControlFile(@NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final ContentEncoding contentEncoding, @NotNull final AddressPair<SmtpAddress> smtpAddresses, @NotNull final AddressPair<DtsName> dtsNames)
	{
		return new ControlFile
		(
			VersionMajor1Minor0,
			determineAddressType(smtpAddresses, dtsNames),
			Data,
			workflowIdentifier,
			smtpAddresses.a,
			dtsNames.a,
			smtpAddresses.b,
			dtsNames.b,
			subject,
			localIdentifier,
			dtsIdentifier,
			UnknownProcessIdentifier,
			contentEncoding.compression,
			contentEncoding.encryption,
			UnknownBooleanFlag,
			UnknownDataChecksum,
			UnknownPartnerIdentifier,
			UnknownStatusRecordInstance
		);
	}

	@NotNull
	public final Version version;
	@NotNull
	public final AddressType addressType;
	@NotNull
	public final MessageType messageType;
	@NotNull
	public final WorkflowIdentifier workflowIdentifier;
	@NotNull
	public final SmtpAddress fromSmtpAddress;
	@NotNull
	public final DtsName fromDtsName;
	@NotNull
	public final SmtpAddress toSmtpAddress;
	@NotNull
	public final DtsName toDtsName;
	@NotNull
	public final Subject subject;
	@NotNull
	public final LocalIdentifier localIdentifier;
	@NotNull
	public final DtsIdentifier dtsIdentifier;
	@NotNull
	public final ProcessIdentifier processIdentifier;
	@NotNull
	public final BooleanFlag compress;
	@NotNull
	public final BooleanFlag encrypted;
	@NotNull
	public final BooleanFlag isCompressed;
	@NotNull
	public final DataChecksum dataChecksum;
	@NotNull
	public final PartnerIdentifier partnerIdentifier;
	@NotNull
	public final StatusRecord statusRecord;

	@SuppressWarnings({"ConstructorWithTooManyParameters", "FeatureEnvy", "OverlyCoupledMethod"})
	public ControlFile(@NotNull final Version version, @NotNull final AddressType addressType, @NotNull final MessageType messageType, @NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final SmtpAddress fromSmtpAddress, @NotNull final DtsName fromDtsName, @NotNull final SmtpAddress toSmtpAddress, @NotNull final DtsName toDtsName, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final ProcessIdentifier processIdentifier, @NotNull final BooleanFlag compress, @NotNull final BooleanFlag encrypted, @NotNull final BooleanFlag isCompressed, @NotNull final DataChecksum dataChecksum, @NotNull final PartnerIdentifier partnerIdentifier, @NotNull final StatusRecord statusRecord)
	{
		this.version = version;
		this.addressType = addressType;
		this.messageType = messageType;
		this.workflowIdentifier = workflowIdentifier;
		this.fromSmtpAddress = fromSmtpAddress;
		this.fromDtsName = fromDtsName;
		this.toSmtpAddress = toSmtpAddress;
		this.toDtsName = toDtsName;
		this.subject = subject;
		this.localIdentifier = localIdentifier;
		this.dtsIdentifier = dtsIdentifier;
		this.processIdentifier = processIdentifier;
		this.compress = compress;
		this.encrypted = encrypted;
		this.isCompressed = isCompressed;
		this.dataChecksum = dataChecksum;
		this.partnerIdentifier = partnerIdentifier;
		this.statusRecord = statusRecord;
		if (addressType.isFromSmtpAddressRequiredAndMissing(fromSmtpAddress))
		{
			throw new IllegalArgumentException("fromSmtpAddress is required for address type");
		}
		if (addressType.isFromDtsNameRequiredAndMissing(fromDtsName))
		{
			throw new IllegalArgumentException("fromDtsName is required for address type");
		}
		if (addressType.isToSmtpAddressRequiredAndMissing(toSmtpAddress))
		{
			throw new IllegalArgumentException("toSmtpAddress is required for address type");
		}
		if (addressType.isToDtsNameRequiredAndMissing(toDtsName))
		{
			throw new IllegalArgumentException("toDtsName is required for address type");
		}
		if (workflowIdentifier.isUnknown())
		{
			throw new IllegalArgumentException("workflowIdentifier is mandatory");
		}
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("Version", version);
			mapSerialiser.writeProperty("AddressType", addressType);
			mapSerialiser.writeProperty("MessageType", messageType);
			mapSerialiser.writeProperty("WorkflowId", workflowIdentifier);
			if (addressType.isFromSmtpAddressRequired())
			{
				mapSerialiser.writeProperty("From_ESMTP", fromSmtpAddress);
			}
			if (addressType.isFromDtsNameRequired())
			{
				mapSerialiser.writeProperty("From_DTS", fromDtsName);
			}
			if (addressType.isToSmtpAddressRequired())
			{
				mapSerialiser.writeProperty("To_ESMTP", toSmtpAddress);
			}
			if (addressType.isToDtsNameRequired())
			{
				mapSerialiser.writeProperty("To_DTS", toDtsName);
			}
			writePropertyIfKnown(mapSerialiser, "Subject", subject);
			writePropertyIfKnown(mapSerialiser, "LocalId", localIdentifier);
			writePropertyIfKnown(mapSerialiser, "DTSId", dtsIdentifier);
			writePropertyIfKnown(mapSerialiser, "ProcessId", processIdentifier);
			writePropertyIfKnown(mapSerialiser, "Compress", compress);
			writePropertyIfKnown(mapSerialiser, "Encrypted", encrypted);
			writePropertyIfKnown(mapSerialiser, "IsCompressed", isCompressed);
			writePropertyIfKnown(mapSerialiser, "DataChecksum", dataChecksum);
			writePropertyIfKnown(mapSerialiser, "PartnerIdentifier", partnerIdentifier);
			writePropertyIfKnown(mapSerialiser, "StatusRecord", statusRecord);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);

		}
	}

	private static <V extends ValueSerialisable & IsUnknown> void writePropertyIfKnown(final MapSerialiser mapSerialiser, @NonNls final String name, final V value) throws CouldNotWritePropertyException
	{
		if (value.isKnown())
		{
			mapSerialiser.writeProperty(name, value);
		}
	}

	private static <V extends MapSerialisable & IsUnknown> void writePropertyIfKnown(final MapSerialiser mapSerialiser, @NonNls final String name, final V value) throws CouldNotWritePropertyException
	{
		if (value.isKnown())
		{
			mapSerialiser.writeProperty(name, value);
		}
	}

	@SuppressWarnings({"OverlyLongMethod", "FeatureEnvy", "OverlyComplexMethod"})
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

		final ControlFile that = (ControlFile) obj;

		if (addressType != that.addressType)
		{
			return false;
		}
		if (isCompressed != that.isCompressed)
		{
			return false;
		}
		if (compress != that.compress)
		{
			return false;
		}
		if (!dataChecksum.equals(that.dataChecksum))
		{
			return false;
		}
		if (!partnerIdentifier.equals(that.partnerIdentifier))
		{
			return false;
		}
		if (!dtsIdentifier.equals(that.dtsIdentifier))
		{
			return false;
		}
		if (encrypted != that.encrypted)
		{
			return false;
		}
		if (!fromDtsName.equals(that.fromDtsName))
		{
			return false;
		}
		if (!fromSmtpAddress.equals(that.fromSmtpAddress))
		{
			return false;
		}
		if (!localIdentifier.equals(that.localIdentifier))
		{
			return false;
		}
		if (messageType != that.messageType)
		{
			return false;
		}
		if (!processIdentifier.equals(that.processIdentifier))
		{
			return false;
		}
		if (!statusRecord.equals(that.statusRecord))
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
		if (version != that.version)
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
		int result = version.hashCode();
		result = 31 * result + addressType.hashCode();
		result = 31 * result + messageType.hashCode();
		result = 31 * result + fromSmtpAddress.hashCode();
		result = 31 * result + toSmtpAddress.hashCode();
		result = 31 * result + fromDtsName.hashCode();
		result = 31 * result + toDtsName.hashCode();
		result = 31 * result + subject.hashCode();
		result = 31 * result + localIdentifier.hashCode();
		result = 31 * result + dtsIdentifier.hashCode();
		result = 31 * result + workflowIdentifier.hashCode();
		result = 31 * result + processIdentifier.hashCode();
		result = 31 * result + compress.hashCode();
		result = 31 * result + encrypted.hashCode();
		result = 31 * result + dataChecksum.hashCode();
		result = 31 * result + partnerIdentifier.hashCode();
		result = 31 * result + statusRecord.hashCode();
		return result;
	}
}
