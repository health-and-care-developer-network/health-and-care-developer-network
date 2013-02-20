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
import uk.nhs.hdn.ckan.domain.enumerations.Capacity;
import uk.nhs.hdn.ckan.domain.enumerations.State;
import uk.nhs.hdn.ckan.domain.enumerations.Type;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;

@SuppressWarnings("OverlyCoupledClass")
public final class GroupReference extends AbstractToString implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String capacityField = "capacity";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String descriptionField = "description";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String titleField = "title";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String approvalStatusField = "approval_status";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String stateField = "state";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String imageUrlField = "image_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String displayNameField = "display_name";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String revisionIdField = "revision_id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String packagesField = "packages";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String typeField = "type";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String nameField = "name";

	@NotNull public final Capacity capacity;
	@NotNull @NonNls public final String description;
	@NotNull @NonNls public final String title;
	@NotNull public final ApprovalStatus approvalStatus;
	@NotNull public final State state;
	@NotNull public final Url imageUrl;
	@NotNull @NonNls public final String displayName;
	@NotNull public final RevisionId revisionId;
	public final long packages;
	@NotNull public final Type type;
	@NotNull public final GroupId id;
	@NotNull public final GroupName name;

	@SuppressWarnings({"ConstructorWithTooManyParameters", "FeatureEnvy", "OverlyCoupledMethod"})
	public GroupReference(@NotNull final Capacity capacity, @NotNull @NonNls final String description, @NotNull @NonNls final String title, @NotNull final ApprovalStatus approvalStatus, @NotNull final State state, @NotNull final Url imageUrl, @NotNull @NonNls final String displayName, @NotNull final RevisionId revisionId, final long packages, @NotNull final Type type, @NotNull final GroupId id, @NotNull final GroupName name)
	{
		this.capacity = capacity;
		this.description = description;
		this.title = title;
		this.approvalStatus = approvalStatus;
		this.state = state;
		this.imageUrl = imageUrl;
		this.displayName = displayName;
		this.revisionId = revisionId;
		this.packages = packages;
		this.type = type;
		this.id = id;
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
			mapSerialiser.writeProperty(capacityField, capacity);
			mapSerialiser.writeProperty(descriptionField, description);
			mapSerialiser.writeProperty(titleField, description);
			mapSerialiser.writeProperty(approvalStatusField, approvalStatus);
			mapSerialiser.writeProperty(stateField, state);
			mapSerialiser.writeProperty(imageUrlField, imageUrl);
			mapSerialiser.writeProperty(displayNameField, displayName);
			mapSerialiser.writeProperty(revisionIdField, revisionId);
			mapSerialiser.writeProperty(packagesField, packages);
			mapSerialiser.writeProperty(typeField, type);
			mapSerialiser.writeProperty(idField, id);
			mapSerialiser.writeProperty(nameField, name);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings("OverlyComplexMethod")
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

		final GroupReference that = (GroupReference) obj;

		if (packages != that.packages)
		{
			return false;
		}
		if (approvalStatus != that.approvalStatus)
		{
			return false;
		}
		if (capacity != that.capacity)
		{
			return false;
		}
		if (!description.equals(that.description))
		{
			return false;
		}
		if (!title.equals(that.title))
		{
			return false;
		}
		if (!displayName.equals(that.displayName))
		{
			return false;
		}
		if (!id.equals(that.id))
		{
			return false;
		}
		if (!imageUrl.equals(that.imageUrl))
		{
			return false;
		}
		if (!name.equals(that.name))
		{
			return false;
		}
		if (!revisionId.equals(that.revisionId))
		{
			return false;
		}
		if (state != that.state)
		{
			return false;
		}
		if (type != that.type)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = capacity.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + title.hashCode();
		result = 31 * result + approvalStatus.hashCode();
		result = 31 * result + state.hashCode();
		result = 31 * result + imageUrl.hashCode();
		result = 31 * result + displayName.hashCode();
		result = 31 * result + revisionId.hashCode();
		result = 31 * result + (int) (packages ^ (packages >>> 32));
		result = 31 * result + type.hashCode();
		result = 31 * result + id.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
