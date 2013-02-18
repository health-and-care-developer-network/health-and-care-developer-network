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

package uk.nhs.hdn.ckan.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.DatasetId;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractListCollectingStringArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.util.List;

import static uk.nhs.hdn.ckan.parsing.DatasetIdArrayCreator.DatasetIdArray;
import static uk.nhs.hdn.ckan.domain.DatasetId.valueOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;

public final class DatasetIdsArrayJsonSchema extends ArrayJsonSchema<DatasetId>
{
	@NotNull
	public static final ArrayRootArrayConstructor<DatasetId> DatasetIdsSchema = rootIsArrayOf
	(
		DatasetIdArray,
		new AbstractListCollectingStringArrayConstructor<DatasetId>(DatasetIdArray)
		{
			@Override
			public void addConstantStringValue(@NotNull final List<DatasetId> arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
			{
				final DatasetId datasetIdUuid;
				try
				{
					datasetIdUuid = valueOf(value);
				}
				catch (RuntimeException e)
				{
					throw new SchemaViolationInvalidJsonException("Could not convert to a DatasetId", e);
				}
				arrayCollector.add(datasetIdUuid);
			}
		}
	);

	@NotNull
	public static final ArrayJsonSchema<DatasetId> DatasetIdsUuidSchemaInstance = new DatasetIdsArrayJsonSchema();

	private DatasetIdsArrayJsonSchema()
	{
		super(DatasetIdsSchema);
	}

}
