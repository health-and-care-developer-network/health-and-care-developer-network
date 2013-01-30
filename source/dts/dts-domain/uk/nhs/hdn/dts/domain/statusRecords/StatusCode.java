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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;
import uk.nhs.hdn.dts.domain.ZeroPaddedDecimalUnsignedInteger;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.dts.domain.ZeroPaddedDecimalUnsignedInteger.fromPaddedValue;

public final class StatusCode implements ValueSerialisable
{
	@NotNull
	private final ZeroPaddedDecimalUnsignedInteger zeroPaddedDecimalUnsignedInteger;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static StatusCode statusCode(@NonNls @NotNull final CharSequence paddedValue)
	{
		return new StatusCode(fromPaddedValue(paddedValue, 2));
	}

	public StatusCode(@NotNull final ZeroPaddedDecimalUnsignedInteger zeroPaddedDecimalUnsignedInteger)
	{
		if (zeroPaddedDecimalUnsignedInteger.doesNotHaveWidth(2))
		{
			throw new IllegalArgumentException(format(ENGLISH, "zeroPaddedDecimalUnsignedInteger must be of width 2, so %1$s is invalid", zeroPaddedDecimalUnsignedInteger));
		}
		this.zeroPaddedDecimalUnsignedInteger = zeroPaddedDecimalUnsignedInteger;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		zeroPaddedDecimalUnsignedInteger.serialiseValue(valueSerialiser);
	}

	public long value()
	{
		return zeroPaddedDecimalUnsignedInteger.value();
	}

	@NotNull
	public String paddedValue()
	{
		return zeroPaddedDecimalUnsignedInteger.paddedValue();
	}

	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), zeroPaddedDecimalUnsignedInteger.paddedValue());
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

		final StatusCode that = (StatusCode) obj;

		if (!zeroPaddedDecimalUnsignedInteger.equals(that.zeroPaddedDecimalUnsignedInteger))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return zeroPaddedDecimalUnsignedInteger.hashCode();
	}
}
