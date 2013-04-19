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
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.ParserFactory;
import uk.nhs.hdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.store.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.store.recordStore.SubstitutableRecordStore;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyLongFieldParser.NonEmptyLongFieldParserInstance;
import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;
import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyURIFieldParser.NonEmptyURIFieldParserInstance;
import static uk.nhs.hdn.crds.store.domain.metadata.parsing.IdentifierFieldParser.ProviderIdentifierFieldParserInstance;
import static uk.nhs.hdn.crds.store.domain.metadata.parsing.IdentifierFieldParser.RepositoryIdentifierFieldParserInstance;

public final class ProviderAndRepositoryMetadataParserFactory extends AbstractToString implements ParserFactory
{
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore;
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore;

	public ProviderAndRepositoryMetadataParserFactory(@NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore, @NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore)
	{
		this.providerMetadataRecordStore = providerMetadataRecordStore;
		this.repositoryMetadataRecordStore = repositoryMetadataRecordStore;
	}

	@NotNull
	@Override
	public Parser parser()
	{
		final ProviderAndRepositoryMetadataRecordLinesParser providerAndRepositoryMetadataRecordLinesParser = new ProviderAndRepositoryMetadataRecordLinesParser(providerMetadataRecordStore, repositoryMetadataRecordStore);
		return new TabSeparatedValueParser
		(
			new ToDomainSeparatedValueParseEventHandler<>
			(
				providerAndRepositoryMetadataRecordLinesParser,
				providerAndRepositoryMetadataRecordLinesParser,

				ProviderIdentifierFieldParserInstance, // providerIdentifier
				NonEmptyLongFieldParserInstance, // providerLastModified
				NonEmptyStringFieldParserInstance, // organisationalDataServiceCode
				RepositoryIdentifierFieldParserInstance, // repositoryIdentifier
				NonEmptyLongFieldParserInstance, // repositoryLastModified
				NonEmptyStringFieldParserInstance, // systemDescription
				NonEmptyURIFieldParserInstance // systemLocation
			)
		);
	}

}
