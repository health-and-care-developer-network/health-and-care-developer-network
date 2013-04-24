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

package uk.nhs.hdn.crds.registry.domain.metadata.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.ParserFactory;
import uk.nhs.hdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.ToDomainSeparatedValueParseEventHandler;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.registry.recordStore.SubstitutableRecordStore;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyLongFieldParser.NonEmptyLongFieldParserInstance;
import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;
import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyURIFieldParser.NonEmptyURIFieldParserInstance;
import static uk.nhs.hdn.crds.registry.domain.metadata.parsing.IdentifierFieldParser.ProviderIdentifierFieldParserInstance;
import static uk.nhs.hdn.crds.registry.domain.metadata.parsing.IdentifierFieldParser.RepositoryIdentifierFieldParserInstance;
import static uk.nhs.hdn.crds.registry.domain.metadata.parsing.IdentifierFieldParser.StuffIdentifierFieldParserInstance;

public final class MetadataRecordsParserFactory extends AbstractToString implements ParserFactory
{
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore;
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore;
	@NotNull private final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> stuffMetadataRecordStore;

	public MetadataRecordsParserFactory(@NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore, @NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore, @NotNull final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> stuffMetadataRecordStore)
	{
		this.providerMetadataRecordStore = providerMetadataRecordStore;
		this.repositoryMetadataRecordStore = repositoryMetadataRecordStore;
		this.stuffMetadataRecordStore = stuffMetadataRecordStore;
	}

	@NotNull
	@Override
	public Parser parser()
	{
		final MetadataRecordsLinesParser metadataRecordsLinesParser = new MetadataRecordsLinesParser(providerMetadataRecordStore, repositoryMetadataRecordStore, stuffMetadataRecordStore);
		return new TabSeparatedValueParser
		(
			new ToDomainSeparatedValueParseEventHandler<>
			(
					metadataRecordsLinesParser,
					metadataRecordsLinesParser,

				ProviderIdentifierFieldParserInstance, // providerIdentifier
				NonEmptyLongFieldParserInstance, // providerLastModified
				NonEmptyStringFieldParserInstance, // organisationalDataServiceCode
				RepositoryIdentifierFieldParserInstance, // repositoryIdentifier
				NonEmptyLongFieldParserInstance, // repositoryLastModified
				NonEmptyStringFieldParserInstance, // systemDescription
				NonEmptyURIFieldParserInstance, // systemLocation
				StuffIdentifierFieldParserInstance, // stuffIdentifier
				NonEmptyLongFieldParserInstance, // stuffLastModified
				NonEmptyStringFieldParserInstance // stuffDescription
			)
		);
	}

}
