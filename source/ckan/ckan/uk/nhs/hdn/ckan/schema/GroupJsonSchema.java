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
import uk.nhs.hdn.ckan.domain.Group;
import uk.nhs.hdn.ckan.domain.GroupReference;
import uk.nhs.hdn.ckan.domain.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.User;
import uk.nhs.hdn.ckan.domain.enumerations.ApprovalStatus;
import uk.nhs.hdn.ckan.domain.enumerations.Capacity;
import uk.nhs.hdn.ckan.domain.enumerations.State;
import uk.nhs.hdn.ckan.domain.enumerations.Type;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.strings.OpenId;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.ckan.domain.uniqueNames.UserName;
import uk.nhs.hdn.ckan.domain.urls.AbstractUrl;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.PackageId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.ids.UserId;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.parsers.json.ObjectJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ConvertUsingDelegateListCollectingStringArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ObjectRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.FieldExpectation;

import java.util.Map;

import static uk.nhs.hdn.ckan.domain.Group.*;
import static uk.nhs.hdn.ckan.domain.urls.UnknownUrl.UnknownUrlInstance;
import static uk.nhs.hdn.ckan.schema.arrayCreators.GroupArrayCreator.GroupArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.GroupReferenceArrayCreator.GroupReferenceArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.PackageIdArrayCreator.PackageIdArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.UserArrayCreator.UserArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ConvertUsingDelegateListCollectingStringArrayConstructor.convertUsingDelegateListCollectingStringArrayConstructor;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullObjectRootArrayConstructor.rootIsObjectOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.StringGenericObjectConstructor.StringGenericObjectConstructorInstance;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.FieldExpectation.nullableStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullEnumFieldExpectation.nonNullEnumField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nullStringToSomethingElseField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nullableStringToSomethingElseField;

@SuppressWarnings("OverlyCoupledClass")
public final class GroupJsonSchema extends ObjectJsonSchema<Group>
{
	@NotNull
	public static final ConvertUsingDelegateListCollectingStringArrayConstructor<PackageId> ArrayOfPackageIdsConstructor = convertUsingDelegateListCollectingStringArrayConstructor(PackageIdArray, PackageId.class, "valueOf");

	// Remember, FieldExpectations mutate
	public static FieldExpectation<Capacity> capacityFieldExpectation()
	{
		return nonNullStringToSomethingElseField("capacity", Capacity.class, Capacity.class, "capacity");
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static final ObjectRootArrayConstructor<Group> GroupSchema = rootIsObjectOf
	(
		GroupArray,
		object
		(
			Group.class,
			nonNullField(usersField, User[].class, nonNullArrayOfObjects
			(
				UserArray,
				object
				(
					User.class,
					nullStringToSomethingElseField("openid", OpenId.class),
					nullableStringField("about"),
					capacityFieldExpectation(),
					nonNullStringToSomethingElseField(nameField, UserName.class),
					nonNullStringToSomethingElseField(createdField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, "microsecondTimestamp"),
					nonNullStringToSomethingElseField("email_hash", Hash.class),
					nonNullField("number_of_edits", long.class),
					nonNullField("number_administered_packages", long.class),
					nonNullStringField(displayNameField),
					nonNullStringField("fullname"),
					nonNullStringToSomethingElseField(idField, UserId.class, UserId.class, "valueOf")
				)
			)),
			nonNullStringField(displayNameField),
			nullableStringField(descriptionField),
			nonNullStringField(titleField),
			nonNullStringToSomethingElseField(createdField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, "microsecondTimestamp"),
			nonNullEnumField(approvalStatusField, ApprovalStatus.class),
			nonNullEnumField(stateField, State.class),
			nonNullField(extrasField, Map.class, StringGenericObjectConstructorInstance),
			nullableStringToSomethingElseField(imageUrlField, Url.class, AbstractUrl.class, "parseUrl", UnknownUrlInstance),
			nonNullField(groupsField, GroupReference[].class, nonNullArrayOfObjects
			(
				GroupReferenceArray,
				object
				(
					GroupReference.class,
					capacityFieldExpectation(),
					nullableStringField(descriptionField),
					nonNullStringField(titleField),
					nonNullEnumField(approvalStatusField, ApprovalStatus.class),
					nonNullEnumField(stateField, State.class),
					nullableStringToSomethingElseField(imageUrlField, Url.class, AbstractUrl.class, "parseUrl", UnknownUrlInstance),
					nonNullStringField(displayNameField),
					nonNullStringToSomethingElseField(revisionIdField, RevisionId.class, RevisionId.class, "valueOf"),
					nonNullField(packagesField, long.class),
					nonNullEnumField(typeField, Type.class),
					nonNullStringToSomethingElseField(idField, GroupId.class, GroupId.class, "valueOf"),
					nonNullStringToSomethingElseField(nameField, GroupName.class)
				)
			)),
			nonNullStringToSomethingElseField(revisionIdField, RevisionId.class, RevisionId.class, "valueOf"),
			nonNullField(packagesField, PackageId[].class, ArrayOfPackageIdsConstructor),
			nonNullEnumField(typeField, Type.class),
			nonNullStringToSomethingElseField(idField, GroupId.class, GroupId.class, "valueOf"),
			nonNullField(tagsField, TagName[].class, TagsArrayJsonSchema.ArrayOfTagsConstructor),
			nonNullStringToSomethingElseField(nameField, GroupName.class)
		)
	);

	@NotNull
	public static final JsonSchema<Group> GroupSchemaInstance = new GroupJsonSchema();

	private GroupJsonSchema()
	{
		super(GroupSchema);
	}
}
