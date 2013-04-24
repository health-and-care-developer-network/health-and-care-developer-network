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
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.reflection.toString.delegates.EnumMethodDelegate;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.crds.registry.domain.metadata.RepositoryMetadataRecord;

import java.net.URI;

import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonEmptyStringFieldExpectation.nonEmptyStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.common.reflection.toString.delegates.ConstructorDelegate.constructorDelegate;
import static uk.nhs.hdn.crds.registry.client.jsonSchemas.arrayCreators.RepositoryMetadataRecordArrayCreator.RepositoryMetadataRecordArray;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Repository;

public final class RepositoryMetadataRecordArrayJsonSchema extends ArrayJsonSchema<RepositoryMetadataRecord>
{
	@NotNull
	public static final ArrayRootArrayConstructor<RepositoryMetadataRecord> RepositoryMetadataRecordsSchema = rootIsArrayOf
	(
		RepositoryMetadataRecordArray,
		nonNullArrayOfObjects
		(
			RepositoryMetadataRecordArray,
			object
			(
				RepositoryMetadataRecord.class,
				nonNullStringToSomethingElseField("identifier", RepositoryIdentifier.class, EnumMethodDelegate.<IdentifierConstructor, RepositoryIdentifier>enumMethodDelegate(Repository, "construct", String.class), false),
				nonNulllongField("lastModified"),
				nonEmptyStringField("systemDescription"),
				nonNullStringToSomethingElseField("systemLocation", URI.class, constructorDelegate(URI.class, String.class), false)
			)
		)
	);

	@NotNull
	public static final ArrayJsonSchema<RepositoryMetadataRecord> RepositoryMetadataRecordsSchemaUsingParserInstance = new RepositoryMetadataRecordArrayJsonSchema();

	private RepositoryMetadataRecordArrayJsonSchema()
	{
		super(RepositoryMetadataRecordsSchema);
	}
}
