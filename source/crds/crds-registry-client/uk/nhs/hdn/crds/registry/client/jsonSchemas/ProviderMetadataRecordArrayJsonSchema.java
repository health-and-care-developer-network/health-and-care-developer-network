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

package uk.nhs.hdn.crds.registry.client.jsonSchemas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractStringArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.reflection.toString.delegates.EnumMethodDelegate;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.crds.registry.domain.metadata.ProviderMetadataRecord;

import java.util.Collection;

import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonEmptyStringFieldExpectation.nonEmptyStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.crds.registry.client.jsonSchemas.arrayCreators.ProviderMetadataRecordArrayCreator.ProviderMetadataRecordArray;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Repository;

public final class ProviderMetadataRecordArrayJsonSchema extends ArrayJsonSchema<ProviderMetadataRecord>
{
	@NotNull
	public static final ArrayRootArrayConstructor<ProviderMetadataRecord> ProviderMetadataRecordsSchema = rootIsArrayOf
	(
		ProviderMetadataRecordArray,
		nonNullArrayOfObjects
		(
			ProviderMetadataRecordArray,
			object
			(
				ProviderMetadataRecord.class,
				nonNullStringToSomethingElseField("identifier", ProviderIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, ProviderIdentifier>enumMethodDelegate(Provider, "construct", String.class), false),
				nonNulllongField("lastModified"),
				nonEmptyStringField("organisationalDataServiceCode"),
				NonNullFieldExpectation.nonNullField
				(
						"repositoryIdentifiers",
						RepositoryIdentifier.class,
						new AbstractStringArrayConstructor<Object>()
						{
							@Override
							public void addConstantStringValue(@NotNull final Object arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
							{
								final RepositoryIdentifier identifier;
								try
								{
									identifier = (RepositoryIdentifier) Repository.construct(value);
								}
								catch (RuntimeException e)
								{
									throw new SchemaViolationInvalidJsonException("Not a valid repositoryIdentifier", e);
								}
								@SuppressWarnings("unchecked") final Collection<RepositoryIdentifier> repositoryIdentifiers = (Collection<RepositoryIdentifier>) arrayCollector;
								repositoryIdentifiers.add(identifier);
							}

							@NotNull
							@Override
							public Object newCollector()
							{
								return new HazelcastAwareLinkedHashSet<RepositoryIdentifier>(100);
							}

							@Nullable
							@Override
							public Object collect(@NotNull final Object collector)
							{
								return collector;
							}
						}
				)
			)
		)
	);

	@NotNull
	public static final ArrayJsonSchema<ProviderMetadataRecord> ProviderMetadataRecordsSchemaUsingParserInstance = new ProviderMetadataRecordArrayJsonSchema();

	private ProviderMetadataRecordArrayJsonSchema()
	{
		super(ProviderMetadataRecordsSchema);
	}
}
