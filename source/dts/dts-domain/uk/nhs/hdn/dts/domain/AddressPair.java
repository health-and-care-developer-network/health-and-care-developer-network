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

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.common.unknown.IsUnknown;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.dts.domain.DtsName.UnknownDtsName;
import static uk.nhs.hdn.dts.domain.SmtpAddress.UnknownSmtpAddress;

public final class AddressPair<A extends Address> extends Pair<A, A> implements IsUnknown
{
	@NotNull
	public static final AddressPair<SmtpAddress> UnknownSmtpAddresses = new AddressPair<>(UnknownSmtpAddress, UnknownSmtpAddress);

	@NotNull
	public static final AddressPair<DtsName> UnknownDtsNames = new AddressPair<>(UnknownDtsName, UnknownDtsName);

	public AddressPair(@NotNull final A from, @NotNull final A to)
	{
		super(from, to);
		if (from.isDifferentKnownessAs(to))
		{
			throw new IllegalArgumentException(format(ENGLISH, "from %1$s is not the same knowness as to %2$s", from, to));
		}
	}

	@Override
	public boolean isUnknown()
	{
		return a.isUnknown();
	}

	@Override
	public boolean isKnown()
	{
		return a.isKnown();
	}

	@Override
	public boolean isSameKnownessAs(@NotNull final IsUnknown that)
	{
		if (isUnknown())
		{
			return that.isUnknown();
		}
		else
		{
			return that.isKnown();
		}
	}

	@Override
	public boolean isDifferentKnownessAs(@NotNull final IsUnknown that)
	{
		return !isSameKnownessAs(that);
	}
}
