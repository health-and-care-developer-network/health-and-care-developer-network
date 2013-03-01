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
import uk.nhs.hdn.ckan.domain.Revision;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;

import static uk.nhs.hdn.ckan.schema.RevisionJsonSchema.RevisionObjectConstructor;
import static uk.nhs.hdn.ckan.schema.arrayCreators.RevisionArrayCreator.RevisionArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;

public final class RevisionsArrayJsonSchema extends ArrayJsonSchema<Revision>
{

	@NotNull
	public static final ArrayRootArrayConstructor<Revision> RevisionsSchema = rootIsArrayOf
	(
		RevisionArray,
		nonNullArrayOfObjects(RevisionArray, RevisionObjectConstructor)
	);

	@NotNull
	public static final ArrayJsonSchema<Revision> RevisionsSchemaInstance = new RevisionsArrayJsonSchema();

	private RevisionsArrayJsonSchema()
	{
		super(RevisionsSchema);
	}
}
