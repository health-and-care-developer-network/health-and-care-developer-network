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
import uk.nhs.hdn.ckan.domain.GroupId;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractListCollectingStringArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.util.List;

import static uk.nhs.hdn.ckan.domain.GroupId.valueOf;
import static uk.nhs.hdn.ckan.parsing.GroupIdArrayCreator.GroupIdArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;

public final class GroupIdsArrayJsonSchema extends ArrayJsonSchema<GroupId>
{
	@NotNull
	public static final ArrayRootArrayConstructor<GroupId> GroupIdsSchema = rootIsArrayOf
	(
		GroupIdArray,
		new AbstractListCollectingStringArrayConstructor<GroupId>(GroupIdArray)
		{
			@Override
			public void addConstantStringValue(@NotNull final List<GroupId> arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
			{
				final GroupId datasetIdUuid;
				try
				{
					datasetIdUuid = valueOf(value);
				}
				catch (RuntimeException e)
				{
					throw new SchemaViolationInvalidJsonException("Could not convert to a GroupId", e);
				}
				arrayCollector.add(datasetIdUuid);
			}
		}
	);

	@NotNull
	public static final ArrayJsonSchema<GroupId> GroupIdsUuidSchemaInstance = new GroupIdsArrayJsonSchema();

	private GroupIdsArrayJsonSchema()
	{
		super(GroupIdsSchema);
	}

}
