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

package uk.nhs.hcdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.Description;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.ValueSerialisable;
import uk.nhs.hcdn.common.serialisers.ValueSerialiser;

import java.util.HashMap;
import java.util.Map;

public enum ResponseStatus implements ActualName, Description, ValueSerialisable
{
	Ok("OK", "Exactly one match was found"),
	NoMatch("No Match", "No Matches were found"),
	Failed("Failed", "More than one DTS record matched"),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final Map<String, ResponseStatus> Index = new HashMap<>(3);
	}

	@NonNls
	@NotNull
	private final String actualName;
	@NotNull
	private final String description;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	ResponseStatus(@NonNls @NotNull final String actualName, @NonNls @NotNull final String description)
	{
		this.actualName = actualName;
		this.description = description;
		CompilerWorkaround.Index.put(actualName, this);
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

	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@NotNull
	@Override
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

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static ResponseStatus responseStatus(@NotNull final String value)
	{
		return CompilerWorkaround.Index.get(value);
	}
}
