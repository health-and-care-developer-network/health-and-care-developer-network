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
import uk.nhs.hdn.ckan.domain.Licence;
import uk.nhs.hdn.ckan.domain.enumerations.State;
import uk.nhs.hdn.ckan.domain.uniqueNames.LicenceId;
import uk.nhs.hdn.ckan.domain.urls.AbstractUrl;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.parsers.json.ArrayJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation;

import static uk.nhs.hdn.ckan.domain.Licence.*;
import static uk.nhs.hdn.ckan.domain.urls.UnknownUrl.UnknownUrlInstance;
import static uk.nhs.hdn.ckan.schema.arrayCreators.LicenceArrayCreator.LicenceArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullEnumFieldExpectation.nonNullEnumField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullbooleanField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nullableStringToSomethingElseField;

public final class LicencesArrayJsonSchema extends ArrayJsonSchema<Licence>
{
	@SuppressWarnings("unchecked")
	@NotNull
	public static final ArrayRootArrayConstructor<Licence> LicencesSchema = rootIsArrayOf
	(
		LicenceArray,
		nonNullArrayOfObjects
		(
			LicenceArray,
			object
			(
				Licence.class,
				nonNullEnumField(statusField, State.class),
				nonNullStringField(maintainerField),
				nonNullStringField(familyField),
				nonNullStringField(titleField),
				nullableStringToSomethingElseField(urlField, Url.class, AbstractUrl.class, "parseUrl", UnknownUrlInstance),
				new NonNullFieldExpectation(isGenericField, boolean.class, null, null, false),
				nonNullbooleanField(isOkdCompliantField),
				nonNullbooleanField(isOsiCompliantField),
				nonNullbooleanField(domainDataField),
				nonNullbooleanField(domainContentField),
				nonNullbooleanField(domainSoftwareField),
				nonNullStringToSomethingElseField(idField, LicenceId.class)
			)
		)
	);

	@NotNull
	public static final ArrayJsonSchema<Licence> LicencesSchemaInstance = new LicencesArrayJsonSchema();

	private LicencesArrayJsonSchema()
	{
		super(LicencesSchema);
	}
}
