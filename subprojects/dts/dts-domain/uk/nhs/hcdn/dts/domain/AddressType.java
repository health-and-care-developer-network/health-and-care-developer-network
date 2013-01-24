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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.naming.Description;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.ValueSerialisable;
import uk.nhs.hcdn.common.serialisers.ValueSerialiser;

import java.util.HashMap;
import java.util.Map;

public enum AddressType implements Description, ValueSerialisable
{
	SMTP(true, true, false, false, "SMTP address"),
	DTS(false, false, true, true, "DTSname addressing"),
	ALL(true, true, true, true, "Used by DTS Server when sending to DTS Client. All address fields will be completed"),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final Map<String, AddressType> Index = new HashMap<>(3);
	}

	private final boolean fromSmtpAddressRequired;
	private final boolean toSmtpAddressRequired;
	private final boolean fromDtsNameRequired;
	private final boolean toDtsNameRequired;
	@NonNls
	@NotNull
	private final String description;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	AddressType(final boolean fromSmtpAddressRequired, final boolean toSmtpAddressRequired, final boolean fromDtsNameRequired, final boolean toDtsNameRequired, @NonNls @NotNull final String description)
	{
		this.fromSmtpAddressRequired = fromSmtpAddressRequired;
		this.toSmtpAddressRequired = toSmtpAddressRequired;
		this.fromDtsNameRequired = fromDtsNameRequired;
		this.toDtsNameRequired = toDtsNameRequired;
		this.description = description;
		CompilerWorkaround.Index.put(name(), this);
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(name());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static AddressType addressType(@NotNull final CharSequence value)
	{
		return CompilerWorkaround.Index.get(value);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public static AddressType determineAddressType(@SuppressWarnings("TypeMayBeWeakened") @NotNull final AddressPair<SmtpAddress> smtpAddresses, @SuppressWarnings("TypeMayBeWeakened") @NotNull final AddressPair<DtsName> dtsNames)
	{
		if (smtpAddresses.isKnown())
		{
			if (dtsNames.isKnown())
			{
				return ALL;
			}
			return SMTP;
		}
		if (dtsNames.isKnown())
		{
			return DTS;
		}
		throw new IllegalArgumentException("There are no known smtpAddresses and dtsNames");
	}

	public boolean isFromSmtpAddressRequired()
	{
		return fromSmtpAddressRequired;
	}

	public boolean isToSmtpAddressRequired()
	{
		return toSmtpAddressRequired;
	}

	public boolean isFromDtsNameRequired()
	{
		return fromDtsNameRequired;
	}

	public boolean isToDtsNameRequired()
	{
		return toDtsNameRequired;
	}

	public boolean isFromSmtpAddressRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SmtpAddress fromSmtpAddress)
	{
		return fromSmtpAddressRequired && fromSmtpAddress.isUnknown();
	}

	public boolean isToSmtpAddressRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SmtpAddress toSmtpAddress)
	{
		return toSmtpAddressRequired && toSmtpAddress.isUnknown();
	}

	public boolean isFromDtsNameRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DtsName fromDtsName)
	{
		return fromDtsNameRequired && fromDtsName.isUnknown();
	}

	public boolean isToDtsNameRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DtsName toDtsName)
	{
		return toDtsNameRequired && toDtsName.isUnknown();
	}

	@NotNull
	@Override
	public String description()
	{
		return description;
	}
}
