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

import java.util.HashMap;
import java.util.Map;

public enum AddressType implements Description
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

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static AddressType addressType(@NotNull final CharSequence value)
	{
		return CompilerWorkaround.Index.get(value);
	}

	public boolean isFromSmtpAddressRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SmtpAddress fromSmtpAddress)
	{
		return fromSmtpAddressRequired && fromSmtpAddress.isKnown();
	}

	public boolean isToSmtpAddressRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SmtpAddress toSmtpAddress)
	{
		return toSmtpAddressRequired && toSmtpAddress.isKnown();
	}

	public boolean isFromDtsNameRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DtsName fromDtsName)
	{
		return fromDtsNameRequired && fromDtsName.isKnown();
	}

	public boolean isToDtsNameRequiredAndMissing(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DtsName toDtsName)
	{
		return toDtsNameRequired && toDtsName.isKnown();
	}

	@NotNull
	@Override
	public String description()
	{
		return description;
	}
}
