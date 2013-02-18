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
import uk.nhs.hdn.ckan.domain.strings.DatasetNameString;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.AbstractListCollectingStringArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;

import java.util.List;

import static uk.nhs.hdn.ckan.parsing.DatasetNameArrayCreator.DatasetNameArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;

public final class DatasetNamesArrayJsonSchema extends ArrayJsonSchema<DatasetNameString>
{
	@NotNull
	public static final ArrayRootArrayConstructor<DatasetNameString> DatasetNamesSchema = rootIsArrayOf
	(
		DatasetNameArray,
		new AbstractListCollectingStringArrayConstructor<DatasetNameString>(DatasetNameArray)
		{
			@Override
			public void addConstantStringValue(@NotNull final List<DatasetNameString> arrayCollector, final int index, @NotNull final String value)
			{
				arrayCollector.add(new DatasetNameString(value));
			}
		}
	);

	@NotNull
	public static final ArrayJsonSchema<DatasetNameString> DatasetNamesSchemaInstance = new DatasetNamesArrayJsonSchema();

	private DatasetNamesArrayJsonSchema()
	{
		super(DatasetNamesSchema);
	}

}
