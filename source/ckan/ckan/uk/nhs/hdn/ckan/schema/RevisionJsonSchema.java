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

package uk.nhs.hdn.ckan.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.Revision;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.PackageId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.parsers.json.ObjectJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ObjectRootArrayConstructor;

import static uk.nhs.hdn.ckan.domain.Revision.*;
import static uk.nhs.hdn.ckan.schema.GroupIdsArrayJsonSchema.ArrayOfGroupIdsConstructor;
import static uk.nhs.hdn.ckan.schema.GroupJsonSchema.ArrayOfPackageIdsConstructor;
import static uk.nhs.hdn.ckan.schema.arrayCreators.RevisionArrayCreator.RevisionArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullObjectRootArrayConstructor.rootIsObjectOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.FieldExpectation.nullableStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nullableStringToSomethingElseField;

@SuppressWarnings("OverlyCoupledClass")
public final class RevisionJsonSchema extends ObjectJsonSchema<Revision>
{
	@SuppressWarnings("unchecked")
	@NotNull
	public static final ObjectRootArrayConstructor<Revision> RevisionSchema = rootIsObjectOf
	(
		RevisionArray,
		object
		(
			Revision.class,
			nonNullStringToSomethingElseField(idField, RevisionId.class, RevisionId.class, "valueOf"),
			nonNullStringToSomethingElseField(timestampField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, "microsecondTimestamp"),
			nullableStringField(messageField),
			nullableStringField(authorField),
			nullableStringToSomethingElseField(approvedTimestampField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, "microsecondTimestamp", null),
			nonNullField(packagesField, PackageId[].class, ArrayOfPackageIdsConstructor),
			nonNullField(groupsField, GroupId[].class, ArrayOfGroupIdsConstructor)
		)
	);

	@NotNull
	public static final JsonSchema<Revision> RevisionSchemaInstance = new RevisionJsonSchema();

	private RevisionJsonSchema()
	{
		super(RevisionSchema);
	}
}
