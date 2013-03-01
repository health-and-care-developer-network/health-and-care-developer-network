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
import uk.nhs.hdn.ckan.domain.SearchResult;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.parsers.json.ObjectJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ObjectRootArrayConstructor;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;

import static uk.nhs.hdn.ckan.domain.SearchResult.countField;
import static uk.nhs.hdn.ckan.domain.SearchResult.resultsField;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetNameArrayCreator.DatasetNameArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.SearchResultArrayCreator.SearchResultDatasetNameArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ConvertUsingDelegateListCollectingStringArrayConstructor.convertUsingDelegateListCollectingStringArrayConstructor;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullObjectRootArrayConstructor.rootIsObjectOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;

public final class DatasetNameSearchObjectJsonSchema extends ObjectJsonSchema<SearchResult<DatasetName>>
{
	@SuppressWarnings("unchecked")
	@NotNull
	public static final ObjectRootArrayConstructor<SearchResult<DatasetName>> DatasetNameSearchSchema = rootIsObjectOf
	(
		SearchResultDatasetNameArray,
		object
		(
			SearchResult.class,
			nonNulllongField(countField),
			nonNullField(resultsField, ValueSerialisable[].class, convertUsingDelegateListCollectingStringArrayConstructor(DatasetNameArray, DatasetName.class))
		)
	);

	@NotNull
	public static final ObjectJsonSchema<SearchResult<DatasetName>> DatasetNameSearchSchemaInstance = new DatasetNameSearchObjectJsonSchema();

	private DatasetNameSearchObjectJsonSchema()
	{
		super(DatasetNameSearchSchema);
	}

}
