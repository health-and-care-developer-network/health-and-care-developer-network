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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.Matcher;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class TagCount extends AbstractToString implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String nameField = "name";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String countField = "count";

	@NotNull
	private static final Matcher SeparatedValuesSchema = rootMatcher
	(
		leaf(nameField, 0),
		leaf(countField, 1)
	);

	@NotNull
	private static final String[] SeparatedValuesHeadings =
	{
		nameField,
		countField
	};

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForTagCounts(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@SuppressWarnings("ConditionalExpression")
	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForTagCounts()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NotNull public final TagName name;
	public final long count;

	public TagCount(@NotNull final TagName name, final long count)
	{
		this.name = name;
		this.count = count;
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(nameField, name);
			mapSerialiser.writeProperty(countField, count);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
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

		final TagCount tagCount = (TagCount) obj;

		if (count != tagCount.count)
		{
			return false;
		}
		if (!name.equals(tagCount.name))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + (int) (count ^ (count >>> 32));
		return result;
	}
}
