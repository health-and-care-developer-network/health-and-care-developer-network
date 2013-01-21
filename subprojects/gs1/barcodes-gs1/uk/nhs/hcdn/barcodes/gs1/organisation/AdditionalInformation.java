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

package uk.nhs.hcdn.barcodes.gs1.organisation;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.*;
import uk.nhs.hcdn.common.tuples.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class AdditionalInformation extends AbstractToString implements MapSerialisable
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@SafeVarargs
	@NotNull
	public static AdditionalInformation additionalInformation(@NotNull final Pair<AdditionalInformationKey, Object>... values)
	{
		final EnumMap<AdditionalInformationKey, Object> map = new EnumMap<>(AdditionalInformationKey.class);
		for (final Pair<AdditionalInformationKey, Object> value : values)
		{
			map.put(value.a, value.b);
		}
		return new AdditionalInformation(map);
	}

	@NotNull
	private final Map<AdditionalInformationKey, Object> additionalInformation;

	public AdditionalInformation(@NotNull final Map<AdditionalInformationKey, Object> additionalInformation)
	{
		this.additionalInformation = additionalInformation.isEmpty() ? new EnumMap<>(AdditionalInformationKey.class) : new EnumMap<>(additionalInformation);
	}

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public AdditionalInformation(final boolean defensiveCopy, @NotNull final Map<AdditionalInformationKey, Object> additionalInformation)
	{
		if (defensiveCopy)
		{
			this.additionalInformation = additionalInformation.isEmpty() ? new EnumMap<>(AdditionalInformationKey.class) : new EnumMap<>(additionalInformation);
		}
		else
		{
			this.additionalInformation = additionalInformation;
		}
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		for (final Map.Entry<AdditionalInformationKey, Object> entry : additionalInformation.entrySet())
		{
			try
			{
				mapSerialiser.writeProperty(entry.getKey().name(), entry.getValue());
			}
			catch (CouldNotWritePropertyException e)
			{
				throw new CouldNotSerialiseMapException(this, e);
			}
		}
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

		final AdditionalInformation that = (AdditionalInformation) obj;

		if (!additionalInformation.equals(that.additionalInformation))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return additionalInformation.hashCode();
	}

	@NotNull
	public Set<AdditionalInformationKey> addtionalInformationAvailableForKeys()
	{
		return additionalInformation.keySet();
	}

	@Nullable
	public Object additionalInformationFor(@NotNull @NonNls final AdditionalInformationKey additionalInformationKey)
	{
		return additionalInformation.get(additionalInformationKey);
	}
}
