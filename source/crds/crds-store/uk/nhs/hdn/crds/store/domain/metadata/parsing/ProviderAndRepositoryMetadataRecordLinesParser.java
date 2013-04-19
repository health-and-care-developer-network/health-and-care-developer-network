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

package uk.nhs.hdn.crds.store.domain.metadata.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.LineParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.crds.store.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.store.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.ProviderMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.RepositoryMetadataRecord;
import uk.nhs.hdn.crds.store.recordStore.SubstitutableRecordStore;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class ProviderAndRepositoryMetadataRecordLinesParser implements LinesParser<String, Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>>>, LineParser<String, Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>>>
{
	private static final int SizeGuessForProviders = 1000;
	private static final int RepositoriesPerProviderGuess = 100;
	private static final float OptimumLoadFactor = 0.7f;
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore;
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore;

	public ProviderAndRepositoryMetadataRecordLinesParser(@NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore, @NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore)
	{
		//providerMetadataRecordStore = new HashMap<>(SizeGuessForProviders, OptimumLoadFactor);
		//repositoryMetadataRecordStore = new HashMap<>(SizeGuessForProviders * RepositoriesPerProviderGuess, OptimumLoadFactor);
		this.providerMetadataRecordStore = providerMetadataRecordStore;
		this.repositoryMetadataRecordStore = repositoryMetadataRecordStore;
	}

	@NotNull
	@Override
	public Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>> newParsedLines()
	{
		return new Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>>
		(
			new HashMap<ProviderIdentifier, ProviderMetadataRecord>(SizeGuessForProviders, OptimumLoadFactor),
			new HashMap<RepositoryIdentifier, RepositoryMetadataRecord>(SizeGuessForProviders * RepositoriesPerProviderGuess, OptimumLoadFactor)
		);
	}

	@Override
	public void parse(final int lineIndex, @NotNull final Object[] parsedFields, @NotNull final Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>> parsedLines) throws CouldNotParseLineException
	{
		final ProviderIdentifier providerIdentifier = (ProviderIdentifier) parsedFields[0];
		@MillisecondsSince1970 final long providerLastModified = (long) parsedFields[1];
		final String organisationalDataServiceCode = (String) parsedFields[2];
		final RepositoryIdentifier repositoryIdentifier = (RepositoryIdentifier) parsedFields[3];
		@MillisecondsSince1970 final long repositoryLastModified = (long) parsedFields[4];
		final String systemDescription = (String) parsedFields[5];
		final URI systemLocation = (URI) parsedFields[6];

		// Last modified date wins for provider; equal date, latest value wins
		final Map<ProviderIdentifier, ProviderMetadataRecord> providerMap = parsedLines.a;
		@Nullable final ProviderMetadataRecord providerMetadataRecordCurrent = providerMap.get(providerIdentifier);
		final ProviderMetadataRecord providerMetadataRecordReplacement;
		if (providerMetadataRecordCurrent == null)
		{
			providerMetadataRecordReplacement = new ProviderMetadataRecord(providerIdentifier, providerLastModified, organisationalDataServiceCode, new HazelcastAwareLinkedHashSet<>(repositoryIdentifier));
		}
		else
		{
			providerMetadataRecordReplacement = providerMetadataRecordCurrent.update(providerLastModified, organisationalDataServiceCode, repositoryIdentifier);
		}
		providerMap.put(providerIdentifier, providerMetadataRecordReplacement);

		// Last modified date wins for repository; equal date, latest value wins
		final Map<RepositoryIdentifier, RepositoryMetadataRecord> repositoryMap = parsedLines.b;
		@Nullable final RepositoryMetadataRecord repositoryMetadataRecordCurrent = repositoryMap.get(repositoryIdentifier);
		final RepositoryMetadataRecord repositoryMetadataRecordReplacement;
		if (repositoryMetadataRecordCurrent == null)
		{
			repositoryMetadataRecordReplacement = new RepositoryMetadataRecord(repositoryIdentifier, repositoryLastModified, systemDescription, systemLocation);
		}
		else
		{
			repositoryMetadataRecordReplacement = repositoryMetadataRecordCurrent.update(repositoryLastModified, systemDescription, systemLocation);
		}
		repositoryMap.put(repositoryIdentifier, repositoryMetadataRecordReplacement);
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Override
	public void parse(@MillisecondsSince1970 final long lastModified, @NotNull final Pair<Map<ProviderIdentifier, ProviderMetadataRecord>, Map<RepositoryIdentifier, RepositoryMetadataRecord>> parsedLines)
	{
		final Object compilerHack1 = parsedLines.a;
		providerMetadataRecordStore.substituteNewBackingData((Map<Identifier,AbstractMetadataRecord<?>>) compilerHack1);

		final Object compilerHack2 = parsedLines.b;
		repositoryMetadataRecordStore.substituteNewBackingData((Map<Identifier, AbstractMetadataRecord<?>>) compilerHack2);
	}
}
