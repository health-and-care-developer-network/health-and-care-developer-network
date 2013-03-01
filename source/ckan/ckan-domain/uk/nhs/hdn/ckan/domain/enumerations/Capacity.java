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

package uk.nhs.hdn.ckan.domain.enumerations;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.ActualName;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

import java.util.HashMap;
import java.util.Map;

public enum Capacity implements ValueSerialisable, ActualName
{
	editor,
	admin,
	_public("public"),
	;

	private static final class CompilerWorkaround
	{
		private static final Map<String, Capacity> Index = new HashMap<>(3);
	}

	@NotNull @NonNls private final String actualName;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	Capacity()
	{
		actualName = name();
		CompilerWorkaround.Index.put(actualName, this);
	}

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	Capacity(@NonNls @NotNull final String actualName)
	{
		this.actualName = actualName;
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

	@Nullable
	public static Capacity capacity(@NotNull final String name)
	{
		return CompilerWorkaround.Index.get(name);
	}
}
