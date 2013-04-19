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

package uk.nhs.hdn.crds.store.client.jsonSchemas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractObjectsOnlyForElementsArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.reflection.toString.delegates.EnumMethodDelegate;
import uk.nhs.hdn.crds.store.domain.*;
import uk.nhs.hdn.crds.store.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryEventIdentifier;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.number.NhsNumber;

import java.util.List;

import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullEnumFieldExpectation.nonNullEnumField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.crds.store.client.jsonSchemas.arrayCreators.SimplePatientRecordArrayCreator.SimplePatientRecordArray;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Repository;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.RepositoryEvent;

public final class SimplePatientRecordArrayJsonSchema extends ArrayJsonSchema<SimplePatientRecord>
{
	@NotNull
	public static final ArrayRootArrayConstructor<SimplePatientRecord> SimplePatientRecordsSchema = rootIsArrayOf
	(
		SimplePatientRecordArray,
		nonNullArrayOfObjects
		(
			SimplePatientRecordArray,
			object
			(
				SimplePatientRecord.class,
				nonNullStringToSomethingElseField("patientIdentifier", NhsNumber.class, NhsNumber.class, "valueOf"),
				nonNulllongField("lastModified"),
				nonNullField
				(
					"knownProviders",
					HazelcastAwareLinkedHashMap.class,
					new HazelcastAwareLinkedHashMapObjectConstructor<ProviderIdentifier, ProviderRecord>
					(
						Provider,
						object
						(
							ProviderRecord.class,
							nonNullStringToSomethingElseField("providerIdentifier", ProviderIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, ProviderIdentifier>enumMethodDelegate(Provider, "construct", String.class), false),
							nonNullField
							(
								"knownRepositories",
								HazelcastAwareLinkedHashMap.class,
								new HazelcastAwareLinkedHashMapObjectConstructor<RepositoryIdentifier, RepositoryRecord>
								(
									Repository,
									object
									(
										RepositoryRecord.class,
										nonNullStringToSomethingElseField("repositoryIdentifier", RepositoryIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, RepositoryIdentifier>enumMethodDelegate(Repository, "construct", String.class), false),
										nonNullField
										(
											"repositoryEvents",
											HazelcastAwareLinkedHashSet.class,
											new RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor
											(
												object
												(
													RepositoryEvent.class,
													nonNullStringToSomethingElseField("repositoryEventIdentifier", RepositoryEventIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, RepositoryEventIdentifier>enumMethodDelegate(RepositoryEvent, "construct", String.class), false),
													nonNulllongField("timestamp"),
													nonNullEnumField("repositoryEventKind", RepositoryEventKind.class)
												)
											)
										)
									)
								)
							)
						)
					)
				)
			)
		)
	);


	public void x()
	{


	}

	@NotNull
	public static final ArrayJsonSchema<SimplePatientRecord> SimplePatientRecordsSchemaUsingParserInstance = new SimplePatientRecordArrayJsonSchema();

	private SimplePatientRecordArrayJsonSchema()
	{
		super(SimplePatientRecordsSchema);
	}

	// Could be more efficient by not creating the List first
	private static class RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor extends AbstractObjectsOnlyForElementsArrayConstructor<RepositoryEvent>
	{
		@NotNull private final ObjectConstructor<?> objectConstructor;

		private RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor(@NotNull final ObjectConstructor<?> objectConstructor)
		{
			super(RepositoryEvent.class, false);
			this.objectConstructor = objectConstructor;
		}

		@NotNull
		@Override
		public ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException
		{
			return objectConstructor;
		}

		@Nullable
		@Override
		public Object collect(@NotNull final List<RepositoryEvent> collector) throws SchemaViolationInvalidJsonException
		{
			return new HazelcastAwareLinkedHashSet<>(collector);
		}
	}
}
