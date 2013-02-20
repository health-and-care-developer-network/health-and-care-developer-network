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
import uk.nhs.hdn.ckan.domain.enumerations.Format;
import uk.nhs.hdn.ckan.domain.enumerations.ResourceType;
import uk.nhs.hdn.ckan.domain.ids.PackageId;
import uk.nhs.hdn.ckan.domain.ids.ResourceGroupId;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;

import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writeNullableProperty;

@SuppressWarnings({"ClassWithTooManyFields", "OverlyCoupledClass"})
public final class Resource extends AbstractToString implements Serialisable, MapSerialisable
{
	/*
		"resource_group_id": "0d815062-298b-fe37-3bb2-871951340077",
		"cache_last_updated": "2012-11-13T01:27:13.548912",
		"package_id": "3106a305-43c9-417f-b198-45d257b5698b",
		"webstore_last_updated": null,
		"id": "46bad484-6951-4632-9dc2-62405dbfe5fc",
		"size": "32684",
		"cache_filepath": "/mnt/shared/ckan_resource_cache/46/46bad484-6951-4632-9dc2-62405dbfe5fc/transparency-agenda-1.aspx",
		"last_modified": "2013-02-16T00:54:13.365259",
		"hash": "80eecfc19bd312336edb9e28de17cb28e9ae3da4",
		"description": "NHS Bristol 25k expenditure August 2012",
		"format": "CSV",
		"tracking_summary": {
			"total": 0,
			"recent": 0
		},
		"mimetype_inner": null,
		"mimetype": "text/html",
		"cache_url": "http://data.gov.uk/data/resource_cache/46/46bad484-6951-4632-9dc2-62405dbfe5fc/transparency-agenda-1.aspx",
		"name": null,
		"created": "2012-10-18T16:39:35.577870",
		"url": "http://www.bristol.nhs.uk/about-us/freedom-of-information/transparency-agenda-1.aspx",
		"webstore_url": null,
		"position": 28,
		"resource_type": "file"
	 */

	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String resourceGroupIdField = "resource_group_id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String cacheLastUpdatedField = "cache_last_updated";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String packageIdField = "package_id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String webstoreLastUpdatedField = "webstore_last_updated";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String datastoreActiveField = "datastore_active";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String sizeField = "size";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String cacheFilePathField = "cache_filepath";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String lastModifiedField = "last_modified";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String hashField = "hash";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String descriptionField = "description";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String formatField = "format";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String trackingSummaryField = "tracking_summary";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String mimeTypeInnerField = "mimetype_inner";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String mimeTypeField = "mimetype";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String cacheUrlField = "cache_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String nameField = "name";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String createdField = "created";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String urlField = "url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String webstoreUrlField = "webstore_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String positionField = "position";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String resourceTypeField = "resource_type";

	@NotNull public final ResourceGroupId resourceGroupId;
	@NotNull public final MicrosecondTimestamp cacheLastUpdated;
	@NotNull public final PackageId packageId;
	@Nullable public final MicrosecondTimestamp webstoreLastUpdated;
	@Nullable @NonNls public final String datastoreActive;
	@NotNull public final ResourceId id;
	public final long size;
	@NotNull @NonNls public final String cacheFilePath;
	@NotNull public final MicrosecondTimestamp lastModified;
	@NotNull public final Hash hash;
	@NotNull @NonNls public final String description;
	@NotNull public final Format format;
	@NotNull public final TrackingSummary trackingSummary;
	@Nullable @NonNls public final String mimeTypeInner;
	@NotNull @NonNls public final String mimeType;
	@NotNull public final Url cacheUrl;
	@Nullable @NonNls public final String name;
	@Nullable public final MicrosecondTimestamp created;
	@NotNull public final Url url;
	@Nullable public final Url webstoreUrl;
	public final long position;
	@NotNull public final ResourceType resourceType;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public Resource(@NotNull final ResourceGroupId resourceGroupId, @NotNull final MicrosecondTimestamp cacheLastUpdated, @NotNull final PackageId packageId, @Nullable final MicrosecondTimestamp webstoreLastUpdated, @Nullable @NonNls final String datastoreActive, @NotNull final ResourceId id, final long size, @NotNull @NonNls final String cacheFilePath, @NotNull final MicrosecondTimestamp lastModified, @NotNull final Hash hash, @NotNull @NonNls final String description, @NotNull final Format format, @NotNull final TrackingSummary trackingSummary, @Nullable @NonNls final String mimeTypeInner, @NotNull @NonNls final String mimeType, @NotNull final Url cacheUrl, @Nullable @NonNls final String name, @Nullable final MicrosecondTimestamp created, @NotNull final Url url, @Nullable final Url webstoreUrl, final long position, @NotNull final ResourceType resourceType)
	{
		this.resourceGroupId = resourceGroupId;
		this.cacheLastUpdated = cacheLastUpdated;
		this.packageId = packageId;
		this.webstoreLastUpdated = webstoreLastUpdated;
		this.datastoreActive = datastoreActive;
		this.id = id;
		this.size = size;
		this.cacheFilePath = cacheFilePath;
		this.lastModified = lastModified;
		this.hash = hash;
		this.description = description;
		this.format = format;
		this.trackingSummary = trackingSummary;
		this.mimeTypeInner = mimeTypeInner;
		this.mimeType = mimeType;
		this.cacheUrl = cacheUrl;
		this.name = name;
		this.created = created;
		this.url = url;
		this.webstoreUrl = webstoreUrl;
		this.position = position;
		this.resourceType = resourceType;
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
			mapSerialiser.writeProperty(resourceGroupIdField, resourceGroupId);
			mapSerialiser.writeProperty(cacheLastUpdatedField, cacheLastUpdated);
			mapSerialiser.writeProperty(packageIdField, packageId);
			writeNullableProperty(mapSerialiser, webstoreLastUpdatedField, webstoreLastUpdated);
			writeNullableProperty(mapSerialiser, datastoreActiveField, datastoreActive);
			mapSerialiser.writeProperty(idField, id);
			mapSerialiser.writeProperty(cacheFilePathField, cacheFilePath);
			mapSerialiser.writeProperty(lastModifiedField, lastModified);
			mapSerialiser.writeProperty(hashField, hash);
			mapSerialiser.writeProperty(descriptionField, description);
			mapSerialiser.writeProperty(formatField, format);
			mapSerialiser.writeProperty(trackingSummaryField, trackingSummary);
			writeNullableProperty(mapSerialiser, mimeTypeInnerField, mimeTypeInner);
			mapSerialiser.writeProperty(mimeTypeField, mimeType);
			mapSerialiser.writeProperty(cacheUrlField, cacheUrl);
			writeNullableProperty(mapSerialiser, nameField, name);
			writeNullableProperty(mapSerialiser, createdField, created);
			mapSerialiser.writeProperty(urlField, url);
			writeNullableProperty(mapSerialiser, webstoreUrlField, webstoreUrl);
			mapSerialiser.writeProperty(positionField, position);
			mapSerialiser.writeProperty(resourceTypeField, resourceType);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"FeatureEnvy", "OverlyComplexMethod", "OverlyLongMethod", "ConditionalExpression"})
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

		final Resource resource = (Resource) obj;

		if (position != resource.position)
		{
			return false;
		}
		if (size != resource.size)
		{
			return false;
		}
		if (!cacheFilePath.equals(resource.cacheFilePath))
		{
			return false;
		}
		if (datastoreActive != null ? !datastoreActive.equals(resource.datastoreActive) : resource.datastoreActive != null)
		{
			return false;
		}
		if (!cacheLastUpdated.equals(resource.cacheLastUpdated))
		{
			return false;
		}
		if (!cacheUrl.equals(resource.cacheUrl))
		{
			return false;
		}
		if (created != null ? !created.equals(resource.created) : resource.created != null)
		{
			return false;
		}
		if (!description.equals(resource.description))
		{
			return false;
		}
		if (format != resource.format)
		{
			return false;
		}
		if (!hash.equals(resource.hash))
		{
			return false;
		}
		if (!id.equals(resource.id))
		{
			return false;
		}
		if (!lastModified.equals(resource.lastModified))
		{
			return false;
		}
		if (!mimeType.equals(resource.mimeType))
		{
			return false;
		}
		if (mimeTypeInner != null ? !mimeTypeInner.equals(resource.mimeTypeInner) : resource.mimeTypeInner != null)
		{
			return false;
		}
		if (name != null ? !name.equals(resource.name) : resource.name != null)
		{
			return false;
		}
		if (!packageId.equals(resource.packageId))
		{
			return false;
		}
		if (!resourceGroupId.equals(resource.resourceGroupId))
		{
			return false;
		}
		if (resourceType != resource.resourceType)
		{
			return false;
		}
		if (!trackingSummary.equals(resource.trackingSummary))
		{
			return false;
		}
		if (!url.equals(resource.url))
		{
			return false;
		}
		if (webstoreLastUpdated != null ? !webstoreLastUpdated.equals(resource.webstoreLastUpdated) : resource.webstoreLastUpdated != null)
		{
			return false;
		}
		if (webstoreUrl != null ? !webstoreUrl.equals(resource.webstoreUrl) : resource.webstoreUrl != null)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"FeatureEnvy", "MethodWithMoreThanThreeNegations", "ConditionalExpression"})
	@Override
	public int hashCode()
	{
		int result = resourceGroupId.hashCode();
		result = 31 * result + cacheLastUpdated.hashCode();
		result = 31 * result + packageId.hashCode();
		result = 31 * result + (webstoreLastUpdated != null ? webstoreLastUpdated.hashCode() : 0);
		result = 31 * result + id.hashCode();
		result = 31 * result + (int) (size ^ (size >>> 32));
		result = 31 * result + cacheFilePath.hashCode();
		result = 31 * result + (datastoreActive != null ? datastoreActive.hashCode() : 0);
		result = 31 * result + lastModified.hashCode();
		result = 31 * result + hash.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + format.hashCode();
		result = 31 * result + trackingSummary.hashCode();
		result = 31 * result + (mimeTypeInner != null ? mimeTypeInner.hashCode() : 0);
		result = 31 * result + mimeType.hashCode();
		result = 31 * result + cacheUrl.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		result = 31 * result + url.hashCode();
		result = 31 * result + (webstoreUrl != null ? webstoreUrl.hashCode() : 0);
		result = 31 * result + (int) (position ^ (position >>> 32));
		result = 31 * result + resourceType.hashCode();
		return result;
	}
}
