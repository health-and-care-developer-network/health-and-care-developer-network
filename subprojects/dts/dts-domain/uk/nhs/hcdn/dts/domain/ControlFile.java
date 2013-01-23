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

package uk.nhs.hcdn.dts.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.dts.domain.dtsNames.DtsName;
import uk.nhs.hcdn.dts.domain.identifiers.DtsIdentifier;
import uk.nhs.hcdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hcdn.dts.domain.identifiers.ProcessIdentifier;
import uk.nhs.hcdn.dts.domain.identifiers.WorkflowIdentifier;
import uk.nhs.hcdn.dts.domain.smtpAddresses.SmtpAddress;
import uk.nhs.hcdn.dts.domain.status.StatusRecord;

public final class ControlFile extends AbstractToString
{
	@NotNull
	private final Version version;
	@NotNull
	private final AddressType addressType;
	@NotNull
	private final MessageType messageType;
	@NotNull
	private final SmtpAddress fromSmtpAddress;
	@NotNull
	private final SmtpAddress toSmtpAddress;
	@NotNull
	private final DtsName fromDtsName;
	@NotNull
	private final DtsName toDtsName;
	@NotNull
	private final Subject subject;
	@NotNull
	private final LocalIdentifier localIdentifier;
	@NotNull
	private final DtsIdentifier dtsIdentifier;
	@NotNull
	private final WorkflowIdentifier workflowIdentifier;
	@NotNull
	private final ProcessIdentifier processIdentifier;
	@NotNull
	private final BooleanFlag compress;
	@NotNull
	private final BooleanFlag encrypted;
	@NotNull
	private final DataChecksum dataChecksum;
	@NotNull
	private final StatusRecord statusRecord;

	@SuppressWarnings({"ConstructorWithTooManyParameters", "FeatureEnvy"})
	public ControlFile(@NotNull final Version version, @NotNull final AddressType addressType, @NotNull final MessageType messageType, @NotNull final SmtpAddress fromSmtpAddress, @NotNull final SmtpAddress toSmtpAddress, @NotNull final DtsName fromDtsName, @NotNull final DtsName toDtsName, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final ProcessIdentifier processIdentifier, @NotNull final BooleanFlag compress, @NotNull final BooleanFlag encrypted, @NotNull final DataChecksum dataChecksum, @NotNull final StatusRecord statusRecord)
	{
		this.version = version;
		this.addressType = addressType;
		this.messageType = messageType;
		this.fromSmtpAddress = fromSmtpAddress;
		this.toSmtpAddress = toSmtpAddress;
		this.fromDtsName = fromDtsName;
		this.toDtsName = toDtsName;
		this.subject = subject;
		this.localIdentifier = localIdentifier;
		this.dtsIdentifier = dtsIdentifier;
		this.workflowIdentifier = workflowIdentifier;
		this.processIdentifier = processIdentifier;
		this.compress = compress;
		this.encrypted = encrypted;
		this.dataChecksum = dataChecksum;
		this.statusRecord = statusRecord;
		if (addressType.isFromSmtpAddressRequiredAndMissing(fromSmtpAddress))
		{
			throw new IllegalArgumentException("fromSmtpAddress is required for address type");
		}
		if (addressType.isToSmtpAddressRequiredAndMissing(toSmtpAddress))
		{
			throw new IllegalArgumentException("toSmtpAddress is required for address type");
		}
		if (addressType.isFromDtsNameRequiredAndMissing(fromDtsName))
		{
			throw new IllegalArgumentException("fromDtsName is required for address type");
		}
		if (addressType.isToDtsNameRequiredAndMissing(toDtsName))
		{
			throw new IllegalArgumentException("toDtsName is required for address type");
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
		if (compress != that.compress)
		{
			return false;
		}
		if (!dataChecksum.equals(that.dataChecksum))
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
		result = 31 * result + statusRecord.hashCode();
		return result;
	}
}
