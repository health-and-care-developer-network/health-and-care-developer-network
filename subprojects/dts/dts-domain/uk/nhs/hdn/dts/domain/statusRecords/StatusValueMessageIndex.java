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

package uk.nhs.hdn.dts.domain.statusRecords;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.common.tuples.Quadruple;

import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hdn.common.tuples.Empty.Tuple;
import static uk.nhs.hdn.dts.domain.statusRecords.Event.*;
import static uk.nhs.hdn.dts.domain.statusRecords.Status.ERROR;
import static uk.nhs.hdn.dts.domain.statusRecords.Status.SUCCESS;
import static uk.nhs.hdn.dts.domain.statusRecords.StatusCode.statusCode;
import static uk.nhs.hdn.dts.domain.statusRecords.StatusType.*;

public final class StatusValueMessageIndex extends AbstractToString
{
	private static final String DuplicateClientMessage = "Client authentication failure caused by invalid DTS username or authentication string";

	@NotNull
	public static final StatusValueMessageIndex CurrentKnownStatusValueIndex = new StatusValueMessageIndex
	(
		Tuple.with(Tuple.with(CollectReport).with(COLLECT).with(SUCCESS).with(statusCode("00"))).with("Data collect success confirmation"),
		Tuple.with(Tuple.with(CollectError).with(COLLECT).with(ERROR).with(statusCode("01"))).with("Control file is missing or inaccessible"),
		Tuple.with(Tuple.with(CollectError).with(COLLECT).with(ERROR).with(statusCode("02"))).with("Data file is missing or inaccessible"),
		Tuple.with(Tuple.with(ServerAuthenticationError).with(TRANSFER).with(ERROR).with(statusCode("03"))).with("Server authentication failure caused by an invalid certificate, unreachable certificate path or problem loading the certificate itself."),
		Tuple.with(Tuple.with(TransferReport).with(TRANSFER).with(SUCCESS).with(statusCode("00"))).with("Data transfer success confirmation"),
		Tuple.with(Tuple.with(ClientAuthenticationError).with(TRANSFER).with(ERROR).with(statusCode("04"))).with(DuplicateClientMessage),
		Tuple.with(Tuple.with(DelayReport).with(TRANSFER).with(SUCCESS).with(statusCode("05"))).with("A DelayReport has been produced indicating that the Client has failed to transfer the data file to the DTS Server. The Client will retry again"),
		Tuple.with(Tuple.with(TransferFail).with(TRANSFER).with(ERROR).with(statusCode("06"))).with("Transfer failure. There has been a problem transferring the data file to the DTS Server. The maximum retry attempts have been reached"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("07"))).with("Invalid From address in the control file"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("08"))).with("Invalid To address in the control file"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("09"))).with("Invalid Version of the control file"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("10"))).with("Invalid Address Type in the control file"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("11"))).with("Invalid Message Type for the transfer"),
		Tuple.with(Tuple.with(ServerFail).with(SEND).with(ERROR).with(statusCode("12"))).with("Unregistered To address"),
		Tuple.with(Tuple.with(NonDelivery).with(SEND).with(ERROR).with(statusCode("13"))).with("SMTP delivery failure"),
		Tuple.with(Tuple.with(NonDelivery).with(SEND).with(ERROR).with(statusCode("14"))).with("DTS delivery failure"),
		Tuple.with(Tuple.with(PollReport).with(RECEIVE).with(SUCCESS).with(statusCode("00"))).with("Poll Report Success confirmation"),
		Tuple.with(Tuple.with(PollFail).with(RECEIVE).with(ERROR).with(statusCode("15"))).with("Receive poll error. Check the ssl and dtsclient log files for information that may help to identify the cause"),
		Tuple.with(Tuple.with(ServerAuthenticationError).with(RECEIVE).with(ERROR).with(statusCode("16"))).with("Server authentication failure due to invalid server certificate."),
		Tuple.with(Tuple.with(ClientAuthenticationError).with(RECEIVE).with(ERROR).with(statusCode("17"))).with(DuplicateClientMessage)
	);

	@NotNull
	private final Map<Quadruple<StatusType, Event, Status, StatusCode>, String> descriptions;

	@SafeVarargs
	public StatusValueMessageIndex(@NotNull final Pair<Quadruple<StatusType, Event, Status, StatusCode>, String>... entries)
	{
		descriptions = new HashMap<>(entries.length);
		for (final Pair<Quadruple<StatusType, Event, Status, StatusCode>, String> entry : entries)
		{
			entry.putUniquelyInMap(descriptions);
		}
	}

	@Nullable
	public String findDescription(@NotNull final StatusType statusType, @NotNull final Event event, @NotNull final Status status, @NotNull final StatusCode statusCode)
	{
		return descriptions.get(Tuple.with(statusType).with(event).with(status).with(statusCode));
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

		final StatusValueMessageIndex that = (StatusValueMessageIndex) obj;

		if (!descriptions.equals(that.descriptions))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return descriptions.hashCode();
	}
}
