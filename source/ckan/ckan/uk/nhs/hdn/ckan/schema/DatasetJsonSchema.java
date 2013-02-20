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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.Dataset;
import uk.nhs.hdn.ckan.domain.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.Resource;
import uk.nhs.hdn.ckan.domain.TrackingSummary;
import uk.nhs.hdn.ckan.domain.enumerations.Format;
import uk.nhs.hdn.ckan.domain.enumerations.ResourceType;
import uk.nhs.hdn.ckan.domain.enumerations.State;
import uk.nhs.hdn.ckan.domain.enumerations.Type;
import uk.nhs.hdn.ckan.domain.ids.*;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.ckan.domain.uniqueNames.HubId;
import uk.nhs.hdn.ckan.domain.uniqueNames.LicenceId;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.ckan.domain.urls.AbstractUrl;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.parsers.json.ObjectJsonSchema;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ObjectRootArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation;

import java.util.Map;

import static uk.nhs.hdn.ckan.domain.Dataset.*;
import static uk.nhs.hdn.ckan.domain.Dataset.groupsField;
import static uk.nhs.hdn.ckan.domain.Dataset.idField;
import static uk.nhs.hdn.ckan.domain.Dataset.nameField;
import static uk.nhs.hdn.ckan.domain.Dataset.revisionIdField;
import static uk.nhs.hdn.ckan.domain.Dataset.stateField;
import static uk.nhs.hdn.ckan.domain.Dataset.tagsField;
import static uk.nhs.hdn.ckan.domain.Dataset.titleField;
import static uk.nhs.hdn.ckan.domain.Dataset.trackingSummaryField;
import static uk.nhs.hdn.ckan.domain.Dataset.typeField;
import static uk.nhs.hdn.ckan.domain.Dataset.urlField;
import static uk.nhs.hdn.ckan.domain.Group.createdField;
import static uk.nhs.hdn.ckan.domain.Group.descriptionField;
import static uk.nhs.hdn.ckan.domain.Group.extrasField;
import static uk.nhs.hdn.ckan.domain.Resource.*;
import static uk.nhs.hdn.ckan.domain.TrackingSummary.recentField;
import static uk.nhs.hdn.ckan.domain.TrackingSummary.totalField;
import static uk.nhs.hdn.ckan.domain.urls.UnknownUrl.UnknownUrlInstance;
import static uk.nhs.hdn.ckan.schema.GroupIdsArrayJsonSchema.ArrayOfGroupIdsConstructor;
import static uk.nhs.hdn.ckan.schema.TagsArrayJsonSchema.ArrayOfTagsConstructor;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetArrayCreator.DatasetArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.DatasetNameArrayCreator.DatasetNameArray;
import static uk.nhs.hdn.ckan.schema.arrayCreators.ResourceArrayCreator.ResourceArray;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ConvertUsingDelegateListCollectingStringArrayConstructor.convertUsingDelegateListCollectingStringArrayConstructor;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullObjectRootArrayConstructor.rootIsObjectOf;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.StringGenericObjectConstructor.StringGenericObjectConstructorInstance;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.FieldExpectation.nullableStringField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullEnumFieldExpectation.nonNullEnumField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.*;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation.nonNullField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nonNullStringToSomethingElseField;
import static uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.StringToSomethingElseFieldExpectation.nullableStringToSomethingElseField;
import static uk.nhs.hdn.common.serialisers.ValueSerialisable.NullNumber;

@SuppressWarnings("OverlyCoupledClass")
public final class DatasetJsonSchema extends ObjectJsonSchema<Dataset>
{
	@SuppressWarnings("ConstantNamingConvention") @NotNull @NonNls private static final String valueOf = "valueOf";
	@SuppressWarnings("ConstantNamingConvention") @NotNull @NonNls private static final String parseUrl = "parseUrl";
	@SuppressWarnings("ConstantNamingConvention") @NotNull @NonNls private static final String microsecondTimestamp = "microsecondTimestamp";

	/*
		Format might be an empty string - needs handling
	 */

	@SuppressWarnings("unchecked")
	@NotNull
	public static final ObjectRootArrayConstructor<Dataset> DatasetSchema = rootIsObjectOf
	(
		DatasetArray,
		object
		(
			Dataset.class,
			nullableStringField(licenceTitleField),
			nullableStringField(maintainerField),
			nullableStringField(maintainerEmailField),
			nonNullStringToSomethingElseField(idField, DatasetId.class, DatasetId.class, valueOf),
			nonNullStringToSomethingElseField(metadataCreatedField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp),
			nonNullField(relationshipsField, DatasetName[].class, convertUsingDelegateListCollectingStringArrayConstructor(DatasetNameArray, DatasetName.class)), // WRONG, but ok enough for now
			nullableStringField(licenceField),
			nonNullStringToSomethingElseField(metadataModifiedField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp),
			nullableStringField(authorField),
			nullableStringField(authorEmailField),
			nonNullEnumField(stateField, State.class),
			nullableStringField(versionField),
			nullableStringToSomethingElseField(licenceIdField, LicenceId.class, null),
			nullableStringToSomethingElseField(typeField, Type.class, Type.class, valueOf, null),
			nonNullField(resourcesField, Resource[].class, nonNullArrayOfObjects
			(
				ResourceArray,
				object
				(
					Resource.class,
					nonNullStringToSomethingElseField(resourceGroupIdField, ResourceGroupId.class, ResourceGroupId.class, valueOf),
					nullableStringToSomethingElseField(cacheLastUpdatedField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp, null),
					nonNullStringToSomethingElseField(packageIdField, PackageId.class, PackageId.class, valueOf),
					nullableStringToSomethingElseField(webstoreLastUpdatedField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp, null),
					nullableStringField(datastoreActiveField),
					nonNullStringToSomethingElseField(Resource.idField, ResourceId.class, ResourceId.class, valueOf),
					nullableStringToSomethingElseField(sizeField, long.class, Long.class, valueOf, NullNumber),
					nullableStringField(cacheFilePathField),
					nullableStringToSomethingElseField(lastModifiedField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp, null),
					nonNullStringToSomethingElseField(hashField, Hash.class),
					nonNullStringField(descriptionField),
					nonNullStringToSomethingElseField(formatField, Format.class, Format.class, "format"),
					trackingSummaryField(),
					nullableStringField(mimeTypeInnerField),
					nullableStringField(mimeTypeField),
					nullableStringToSomethingElseField(cacheUrlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
					nullableStringField(Resource.nameField),
					nullableStringToSomethingElseField(createdField, MicrosecondTimestamp.class, MicrosecondTimestamp.class, microsecondTimestamp, null),
					nullableStringToSomethingElseField(urlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
					nullableStringToSomethingElseField(webstoreUrlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
					nonNulllongField(positionField),
					nullableStringToSomethingElseField(resourceTypeField, ResourceType.class, ResourceType.class, valueOf, null),
					nullableStringToSomethingElseField(contentLengthField, long.class, Long.class, valueOf, NullNumber),
					nullableStringToSomethingElseField(opennessScoreField, long.class, Long.class, valueOf, NullNumber),
					nullableStringToSomethingElseField(opennessScoreFailureCountField, long.class, Long.class, valueOf, NullNumber),
					nullableStringField(opennessScoreReasonField),
					nullableStringField(contentTypeField),
					nullableStringToSomethingElseField(hubIdField, HubId.class, null),
					nullableStringField(resourceLocatorProtocolField)
				)
			)),
			nonNullField(tagsField, TagName[].class, ArrayOfTagsConstructor),
			trackingSummaryField(),
			nonNullField(groupsField, GroupId[].class, ArrayOfGroupIdsConstructor),
			nonNullStringToSomethingElseField(nameField, DatasetName.class),
			nonNullbooleanField(isOpenField),
			nonNullStringField(notesRenderedField),
			nullableStringToSomethingElseField(urlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
			nullableStringToSomethingElseField(ckanUrlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
			nonNullStringField(notesField),
			nonNullStringField(titleField),
			nullableStringField(ratingsAverageField),
			nonNullField(extrasField, Map.class, StringGenericObjectConstructorInstance),
			nullableStringToSomethingElseField(licenceUrlField, Url.class, AbstractUrl.class, parseUrl, UnknownUrlInstance),
			nonNulllongField(ratingsCountField),
			nonNullStringToSomethingElseField(revisionIdField, RevisionId.class, RevisionId.class, valueOf)
		)
	);

	@SuppressWarnings("FeatureEnvy")
	private static NonNullFieldExpectation<TrackingSummary> trackingSummaryField()
	{
		return nonNullField(trackingSummaryField, TrackingSummary.class, object
		(
			TrackingSummary.class,
			nonNulllongField(totalField),
			nonNulllongField(recentField)
		));
	}

	@NotNull
	public static final JsonSchema<Dataset> DatasetSchemaInstance = new DatasetJsonSchema();

	private DatasetJsonSchema()
	{
		super(DatasetSchema);
	}
}
