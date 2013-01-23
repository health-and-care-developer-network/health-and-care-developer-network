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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.dts.domain.ZeroPaddedDecimalUnsignedInteger.fromPaddedValue;

public final class TransactionSequenceIdentifier
{
	public static final int Width = 8;
	private static final TransactionSequenceIdentifier BeginningOfSequence = new TransactionSequenceIdentifier(new ZeroPaddedDecimalUnsignedInteger(Width, 1L));
	private static final long Maximum = 99999999L;

	@NotNull
	public static TransactionSequenceIdentifier fromPaddedTransactionSequenceIdentifer(@NotNull final CharSequence value)
	{
		return new TransactionSequenceIdentifier(fromPaddedValue(value, Width));
	}

	@NotNull
	private final ZeroPaddedDecimalUnsignedInteger value;

	public TransactionSequenceIdentifier(@NotNull final ZeroPaddedDecimalUnsignedInteger value)
	{
		this.value = value;
		if (value.doesNotHaveWidth(Width))
		{
			throw new IllegalArgumentException(format(ENGLISH, "A transaction sequence identifier must be 8 digits, not %1$s", value));
		}
		if (value.value() == 0L)
		{
			throw new IllegalArgumentException(format(ENGLISH, "A transaction sequence identifier can not be zero %1$s", value));
		}
	}

	@NotNull
	public TransactionSequenceIdentifier next()
	{
		if (value.value() == Maximum)
		{
			return BeginningOfSequence;
		}
		return new TransactionSequenceIdentifier(value.next());
	}

	@NotNull
	public String toPaddedValue()
	{
		return value.paddedValue();
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), toPaddedValue());
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

		final TransactionSequenceIdentifier that = (TransactionSequenceIdentifier) obj;

		if (!value.equals(that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
