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

package uk.nhs.hdn.crds.store.domain.metadata;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryIdentifier;

import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class RepositoryMetadataRecord extends AbstractMetadataRecord<RepositoryIdentifier>
{
	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForRepositoryMetadataRecords(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForRepositoryMetadataRecords()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		"identifier",
		"lastModified",
		"systemDescription",
		"systemLocation"
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf("identifier", 0),
		leaf("lastModified", 1),
		leaf("systemDescription", 2),
		leaf("systemLocation", 3)
	);

	@NonNls @NotNull public final String systemDescription;
	@NotNull public final URI systemLocation;

	public RepositoryMetadataRecord(@NotNull final RepositoryIdentifier repositoryIdentifier, @MillisecondsSince1970 final long lastModified, @NonNls @NotNull final String systemDescription, @NotNull final URI systemLocation)
	{
		super(repositoryIdentifier, lastModified);
		this.systemDescription = systemDescription;
		this.systemLocation = systemLocation;
	}

	@NotNull
	public RepositoryMetadataRecord update(@MillisecondsSince1970 final long updateLastModified, @NotNull final String updateSystemDescription, @NotNull final URI updateSystemLocation)
	{
		@MillisecondsSince1970 final long latestLastModified;
		@NotNull final String latestSystemDescription;
		@NotNull final URI latestSystemLocation;
		if (updateLastModified >= lastModified)
		{
			latestLastModified = updateLastModified;
			latestSystemDescription = updateSystemDescription;
			latestSystemLocation = updateSystemLocation;
		}
		else
		{
			latestLastModified = lastModified;
			latestSystemDescription = systemDescription;
			latestSystemLocation = systemLocation;
		}
		return new RepositoryMetadataRecord(identifier, latestLastModified, latestSystemDescription, latestSystemLocation);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		super.writeData(out);
		out.writeUTF(systemDescription);
		out.writeUTF(systemLocation.toString());
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		super.serialiseMap(mapSerialiser);
		try
		{
			mapSerialiser.writeProperty("systemDescription", systemDescription);
			mapSerialiser.writeProperty("systemLocation", systemLocation);
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

		final RepositoryMetadataRecord that = (RepositoryMetadataRecord) obj;

		if (!systemDescription.equals(that.systemDescription))
		{
			return false;
		}
		if (!systemLocation.equals(that.systemLocation))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + systemDescription.hashCode();
		result = 31 * result + systemLocation.hashCode();
		return result;
	}
}
