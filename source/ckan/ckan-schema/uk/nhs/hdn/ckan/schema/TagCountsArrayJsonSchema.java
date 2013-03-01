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
import uk.nhs.hdn.ckan.domain.TagCount;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;

import static uk.nhs.hdn.ckan.domain.TagCount.countField;
import static uk.nhs.hdn.ckan.domain.TagCount.nameField;
import static uk.nhs.hdn.ckan.schema.arrayCreators.TagCountArrayCreator.TagCountArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayArraysOnlyForElementsArrayConstructor.nonNullArrayOfArrays;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectFromArrayConstructor.objectFromArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNulllongField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;

public final class TagCountsArrayJsonSchema extends ArrayJsonSchema<TagCount>
{
	@NotNull
	public static final ArrayRootArrayConstructor<TagCount> TagCountsSchema = rootIsArrayOf
	(
		TagCountArray,
		nonNullArrayOfArrays
		(
			TagCountArray,
			objectFromArray
			(
				TagCount.class,
				nonNullStringToSomethingElseField(nameField, TagName.class),
				nonNulllongField(countField)
			)
		)
	);

	@NotNull
	public static final ArrayJsonSchema<TagCount> TagCountsSchemaInstance = new TagCountsArrayJsonSchema();

	private TagCountsArrayJsonSchema()
	{
		super(TagCountsSchema);
	}
}
