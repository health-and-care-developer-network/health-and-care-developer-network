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
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;

import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetIdArrayCreator.DatasetIdArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ConvertUsingDelegateListCollectingStringArrayConstructor.convertUsingDelegateListCollectingStringArrayConstructor;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;

public final class DatasetIdsArrayJsonSchema extends ArrayJsonSchema<DatasetId>
{
	@NotNull
	public static final ArrayRootArrayConstructor<DatasetId> DatasetIdsSchema = rootIsArrayOf
	(
		DatasetIdArray,
		convertUsingDelegateListCollectingStringArrayConstructor(DatasetIdArray, DatasetId.class, "valueOf")
	);

	@NotNull
	public static final ArrayJsonSchema<DatasetId> DatasetIdsSchemaInstance = new DatasetIdsArrayJsonSchema();

	private DatasetIdsArrayJsonSchema()
	{
		super(DatasetIdsSchema);
	}

}
