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

package uk.nhs.hdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.ActualName;
import uk.nhs.hdn.common.naming.Description;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

public enum StatusValue implements ActualName, Description, ValueSerialisable
{
	DtsSendFailed(1, "DTS Send Failed", "The DTS cannot send the Transfer to another DTS Client mailbox or to the SMTP Service"),
	DtsReceivedTransfer(2, "DTS Received Transfer", "The data transfer has been received from the originating client"),
	DtsTransferInRecipientMailbox(3, "DTS Transfer In Recipient Mailbox", "A data Transfer from DTS or SMTP has been deposited in the receiving DTS Mailbox but not yet collected"),
	DtsTransferCollectedByRecipient(4, "DTS Transfer Collected by Recipient", "The receiving DTS Client has collected the data transfer"),
	DtsTransferSentToSmtp(5, "Transfer Sent to SMTP", "The transfer has been sent by the DTS Server into the SMTP mail service"),
	SmtpTransferDelivered(6, "SMTP Transfer Delivered", "The DTS Server can correlate a positive DSN returned from the recipient system with the outgoing message"),
	SmtpTransferFailed(7, "SMTP Transfer Failed", "The DTS Server can correlate a negative DSN returned from the recipient system with the outgoing message"),
	SmtpReceiveFailed(8, "SMTP Receive Failed", "A message received by the DTS but which it is unable to deliver to the recipient mailbox"),
	DtsTransferDeletedNotCollected(9, "DTS Transfer Deleted - Not collected", "The recipient client did not collect the transfer; after a period it was deleted and a non delivery indication sent back to the originator"),
	DtsTransferDeletedMailboxDeleted(10, "DTS Transfer Deleted - Mailbox Deleted", "The recipient client mailbox (containing this message) was deleted; a non delivery indication was sent back to the originator"),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	public static final class CompilerWorkaround
	{
		private static final int MinimumValue = 1;
		private static final int MaximumValue = 10;
		@NotNull
		private static final StatusValue[] Index = new StatusValue[MaximumValue + 1];
	}

	public final int value;
	@NotNull
	private final String actualName;
	@NotNull
	private final String description;

	StatusValue(final int value, @NonNls @NotNull final String actualName, @NonNls @NotNull final String description)
	{
		this.value = value;
		this.actualName = actualName;
		this.description = description;
		CompilerWorkaround.Index[value] = this;
	}


	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@Override
	@NotNull
	public String toString()
	{
		return actualName;
	}

	@NotNull
	@Override
	public String description()
	{
		return description;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(actualName);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static StatusValue statusValue(@NonNls final int value)
	{
		if (value < CompilerWorkaround.MinimumValue || value > CompilerWorkaround.MaximumValue)
		{
			return null;
		}
		return CompilerWorkaround.Index[value];
	}
}
