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
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractObjectsOnlyForElementsArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.reflection.toString.delegates.EnumMethodDelegate;
import uk.nhs.hdn.crds.registry.domain.*;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.number.NhsNumber;

import java.util.List;

import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullEnumFieldExpectation.nonNullEnumField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.crds.registry.client.jsonSchemas.arrayCreators.SimplePatientRecordArrayCreator.SimplePatientRecordArray;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.*;

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
							provider,
						object
						(
							ProviderRecord.class,
							nonNullStringToSomethingElseField("providerIdentifier", ProviderIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, ProviderIdentifier>enumMethodDelegate(provider, "construct", String.class), false),
							nonNullField
							(
								"knownRepositories",
								HazelcastAwareLinkedHashMap.class,
								new HazelcastAwareLinkedHashMapObjectConstructor<RepositoryIdentifier, RepositoryRecord>
								(
										repository,
									object
									(
										RepositoryRecord.class,
										nonNullStringToSomethingElseField("repositoryIdentifier", RepositoryIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, RepositoryIdentifier>enumMethodDelegate(repository, "construct", String.class), false),
										nonNullField
										(
											"stuffEvents",
											HazelcastAwareLinkedHashMap.class,
											new HazelcastAwareLinkedHashMapObjectConstructor<StuffIdentifier, StuffRecord>
											(
													stuff,
												object
												(
													StuffRecord.class,
													nonNullStringToSomethingElseField("stuffIdentifier", StuffIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, StuffIdentifier>enumMethodDelegate(stuff, "construct", String.class), false),
													nonNullField
													(
														"stuffEvents",
														HazelcastAwareLinkedHashSet.class,
														new RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor
														(
															object
															(
																StuffEvent.class,
																nonNullStringToSomethingElseField("repositoryEventIdentifier", StuffEventIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, StuffEventIdentifier>enumMethodDelegate(stuff_event, "construct", String.class), false),
																nonNulllongField("timestamp"),
																nonNullEnumField("repositoryEventKind", StuffEventKind.class)
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
				)
			)
		)
	);

	@NotNull
	public static final ArrayJsonSchema<SimplePatientRecord> SimplePatientRecordsSchemaUsingParserInstance = new SimplePatientRecordArrayJsonSchema();

	private SimplePatientRecordArrayJsonSchema()
	{
		super(SimplePatientRecordsSchema);
	}

	// Could be more efficient by not creating the List first
	private static class RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor extends AbstractObjectsOnlyForElementsArrayConstructor<StuffEvent>
	{
		@NotNull private final ObjectConstructor<?> objectConstructor;

		private RepositoryEventAbstractObjectsOnlyForElementsArrayConstructor(@NotNull final ObjectConstructor<?> objectConstructor)
		{
			super(StuffEvent.class, false);
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
		public Object collect(@NotNull final List<StuffEvent> collector) throws SchemaViolationInvalidJsonException
		{
			return new HazelcastAwareLinkedHashSet<>(collector);
		}
	}
}
