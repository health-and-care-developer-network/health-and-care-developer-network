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

package uk.nhs.hcdn.dts.domain.statusRecords;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.serialisers.MapSerialiser;
import uk.nhs.hcdn.common.unknown.AbstractIsUnknown;
import uk.nhs.hcdn.common.unknown.IsUnknownException;
import uk.nhs.hcdn.dts.domain.statusRecords.dateTime.KnownDateTime;

public final class UnknownStatusRecord extends AbstractIsUnknown implements StatusRecord
{
	@NotNull
	public static final StatusRecord UnknownStatusRecordInstance = new UnknownStatusRecord();

	private UnknownStatusRecord()
	{
		super(true);
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser)
	{
	}

	@NotNull
	@Override
	public KnownDateTime dateTime()
	{
		throw new IsUnknownException();
	}

	@NotNull
	@Override
	public Event event()
	{
		throw new IsUnknownException();
	}

	@NotNull
	@Override
	public Status status()
	{
		throw new IsUnknownException();
	}

	@NotNull
	@Override
	public StatusCode statusCode()
	{
		throw new IsUnknownException();
	}

	@NotNull
	@Override
	public String description()
	{
		throw new IsUnknownException();
	}

	@Override
	public boolean isEvent(@NotNull final Event event)
	{
		return false;
	}

	@Override
	public boolean hasStatus(@NotNull final Status status)
	{
		return false;
	}

	@Override
	public boolean hasStatusCode(@NotNull final StatusCode statusCode)
	{
		return false;
	}
}
