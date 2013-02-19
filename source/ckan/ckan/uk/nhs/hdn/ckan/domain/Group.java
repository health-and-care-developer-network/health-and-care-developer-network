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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.enumerations.ApprovalStatus;
import uk.nhs.hdn.ckan.domain.enumerations.Status;
import uk.nhs.hdn.ckan.domain.enumerations.Type;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.ckan.domain.uuids.GroupId;
import uk.nhs.hdn.ckan.domain.uuids.PackageId;
import uk.nhs.hdn.ckan.domain.uuids.RevisionId;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

@SuppressWarnings("OverlyCoupledClass")
public final class Group implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String usersField = "users";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String displayNameField = "display_name";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String descriptionField = "description";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String titleField = "title";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String createdField = "created";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String approvalStatusField = "approval_status";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String stateField = "state";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String extrasField = "extras";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String imageUrlField = "image_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String groupsField = "groups";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String revisionIdField = "revision_id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String packagesField = "packages";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String typeField = "type";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String tagsField = "tags";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String nameField = "name";

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		usersField,
		displayNameField,
		descriptionField,
		titleField,
		createdField,
		approvalStatusField,
		stateField,
		extrasField,
		imageUrlField,
		groupsField,
		revisionIdField,
		packagesField,
		typeField,
		idField,
		tagsField,
		nameField
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(usersField, 0, ' '),
		leaf(displayNameField, 1),
		leaf(descriptionField, 2),
		leaf(titleField, 3),
		leaf(createdField, 4),
		leaf(approvalStatusField, 5),
		leaf(stateField, 6),
		leaf(extrasField, 7),
		leaf(imageUrlField, 8),
		leaf(groupsField, 9, ' '),
		leaf(revisionIdField, 10),
		leaf(packagesField, 11),
		leaf(typeField, 12),
		leaf(idField, 13),
		leaf(tagsField, 14, ' '),
		leaf(nameField, 15)
	);

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForGroups(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForGroups()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NotNull public final User[] users;
	@NotNull @NonNls public final String displayName;
	@NotNull @NonNls public final String description;
	@NotNull @NonNls public final String title;
	@NotNull public final MicrosecondTimestamp created;
	@NotNull public final ApprovalStatus approvalStatus;
	@NotNull public final Status state;
	@NotNull public final Map<String, Object> extras;
	@NotNull public final Url imageUrl;
	@NotNull public final GroupName[] groups;
	@NotNull public final RevisionId revisionId;
	@NotNull public final PackageId[] packages;
	@NotNull private final Type type;
	@NotNull public final GroupId id;
	@NotNull public final TagName[] tags;
	@NotNull public final GroupName name;
	/*
	"users": [{
		"openid": null,
		"about": null,
		"capacity": "editor",
		"name": "user_d7180",
		"created": "2012-06-28T20:06:30.061645",
		"email_hash": "bd3fb40f748cc3c9c7b6b7a4dffa461c",
		"number_of_edits": 7,
		"number_administered_packages": 0,
		"display_name": "2gether NHS Foundation Trust",
		"fullname": "2gether NHS Foundation Trust",
		"id": "44bfba7e-1ca9-4a27-b98a-b3cc1f1fcccb"
	}],
	"display_name": "2gether NHS Foundation Trust",
	"description": "",
	"title": "2gether NHS Foundation Trust",
	"created": "2012-06-27T14:50:58.676856",
	"approval_status": "approved",
	"state": "active",
	"extras": {
		"contact-name": "",
		"contact-email": "",
		"website-url": "",
		"foi-name": "",
		"abbreviation": "",
		"foi-email": "",
		"website-name": "",
		"contact-phone": "",
		"foi-phone": ""
	},
	"image_url": "",
	"groups": [],
	"revision_id": "a1f4375a-4afe-4dad-88cd-03f2bd6adaaf",
	"packages": ["598c37fa-9d20-465a-988c-a6e31974493a"],
	"type": "publisher",
	"id": "f253ec11-900c-45da-86e0-4dd10f5f6b37",
	"tags": [],
	"name": "2gether-nhs-foundation-trust"
	 */

	@SuppressWarnings({"ConstructorWithTooManyParameters", "FeatureEnvy", "OverlyCoupledMethod"})
	public Group(@NotNull final User[] users, @NotNull @NonNls final String displayName, @NotNull @NonNls final String description, @NotNull @NonNls final String title, @NotNull final MicrosecondTimestamp created, @NotNull final ApprovalStatus approvalStatus, @NotNull final Status state, @NotNull final Map<String, Object> extras, @NotNull final Url imageUrl, @NotNull final GroupName[] groups, @NotNull final RevisionId revisionId, @NotNull final PackageId[] packages, @NotNull final Type type, @NotNull final GroupId id, @NotNull final TagName[] tags, @NotNull final GroupName name)
	{
		this.users = copyOf(users);
		this.displayName = displayName;
		this.description = description;
		this.title = title;
		this.created = created;
		this.approvalStatus = approvalStatus;
		this.state = state;
		this.extras = new HashMap<>(extras);
		this.imageUrl = imageUrl;
		this.groups = copyOf(groups);
		this.revisionId = revisionId;
		this.packages = copyOf(packages);
		this.type = type;
		this.id = id;
		this.tags = copyOf(tags);
		this.name = name;
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(usersField, users);
			mapSerialiser.writeProperty(displayNameField, displayName);
			mapSerialiser.writeProperty(descriptionField, description);
			mapSerialiser.writeProperty(titleField, title);
			mapSerialiser.writeProperty(createdField, created);
			mapSerialiser.writeProperty(approvalStatusField, approvalStatus);
			mapSerialiser.writeProperty(stateField, state);
			mapSerialiser.writeProperty(extrasField, extras);
			mapSerialiser.writeProperty(imageUrlField, imageUrl);
			mapSerialiser.writeProperty(groupsField, groups);
			mapSerialiser.writeProperty(revisionIdField, revisionId);
			mapSerialiser.writeProperty(packagesField, packages);
			mapSerialiser.writeProperty(typeField, type);
			mapSerialiser.writeProperty(idField, id);
			mapSerialiser.writeProperty(tagsField, tags);
			mapSerialiser.writeProperty(nameField, name);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Group group = (Group) obj;

		if (approvalStatus != group.approvalStatus)
		{
			return false;
		}
		if (!created.equals(group.created))
		{
			return false;
		}
		if (!description.equals(group.description))
		{
			return false;
		}
		if (!displayName.equals(group.displayName))
		{
			return false;
		}
		if (!extras.equals(group.extras))
		{
			return false;
		}
		if (!Arrays.equals(groups, group.groups))
		{
			return false;
		}
		if (!id.equals(group.id))
		{
			return false;
		}
		if (!imageUrl.equals(group.imageUrl))
		{
			return false;
		}
		if (!name.equals(group.name))
		{
			return false;
		}
		if (!Arrays.equals(packages, group.packages))
		{
			return false;
		}
		if (!revisionId.equals(group.revisionId))
		{
			return false;
		}
		if (state != group.state)
		{
			return false;
		}
		if (type != group.type)
		{
			return false;
		}
		if (!Arrays.equals(tags, group.tags))
		{
			return false;
		}
		if (!title.equals(group.title))
		{
			return false;
		}
		if (!Arrays.equals(users, group.users))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = Arrays.hashCode(users);
		result = 31 * result + displayName.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + created.hashCode();
		result = 31 * result + approvalStatus.hashCode();
		result = 31 * result + state.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + extras.hashCode();
		result = 31 * result + imageUrl.hashCode();
		result = 31 * result + Arrays.hashCode(groups);
		result = 31 * result + revisionId.hashCode();
		result = 31 * result + Arrays.hashCode(packages);
		result = 31 * result + id.hashCode();
		result = 31 * result + Arrays.hashCode(tags);
		result = 31 * result + name.hashCode();
		return result;
	}
}
