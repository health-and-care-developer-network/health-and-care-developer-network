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

package uk.nhs.hdn.crds.registry.domain.metadata;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;

import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class StuffMetadataRecord extends AbstractMetadataRecord<StuffIdentifier>
{
	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForStuffMetadataRecords(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForStuffMetadataRecords()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		"identifier",
		"lastModified",
		"stuffDescription"
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf("identifier", 0),
		leaf("lastModified", 1),
		leaf("stuffDescription", 2)
	);

	@NonNls @NotNull public final String stuffDescription;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public StuffMetadataRecord(@NotNull final StuffIdentifier stuffIdentifier, @MillisecondsSince1970 final long lastModified, @NonNls @NotNull final String stuffDescription)
	{
		super(stuffIdentifier, lastModified);
		this.stuffDescription = stuffDescription;
	}

	@NotNull
	public StuffMetadataRecord update(@MillisecondsSince1970 final long updateLastModified, @NotNull final String updateStuffDescription)
	{
		@MillisecondsSince1970 final long latestLastModified;
		@NotNull final String latestStuffDescription;
		if (updateLastModified >= lastModified)
		{
			latestLastModified = updateLastModified;
			latestStuffDescription = updateStuffDescription;
		}
		else
		{
			latestLastModified = lastModified;
			latestStuffDescription = stuffDescription;
		}
		return new StuffMetadataRecord(identifier, latestLastModified, latestStuffDescription);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		super.writeData(out);
		out.writeUTF(stuffDescription);
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		super.serialiseMap(mapSerialiser);
		try
		{
			mapSerialiser.writeProperty("stuffDescription", stuffDescription);
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
		if (!super.equals(obj))
		{
			return false;
		}

		final StuffMetadataRecord that = (StuffMetadataRecord) obj;

		if (!stuffDescription.equals(that.stuffDescription))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + stuffDescription.hashCode();
		return result;
	}
}
