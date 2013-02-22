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
import uk.nhs.hdn.ckan.domain.dates.Date;
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.enumerations.ResourceType;
import uk.nhs.hdn.ckan.domain.ids.PackageId;
import uk.nhs.hdn.ckan.domain.ids.ResourceGroupId;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.ckan.domain.strings.Hash;
import uk.nhs.hdn.ckan.domain.uniqueNames.HubId;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;

import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writeNullableProperty;

@SuppressWarnings({"ClassWithTooManyFields", "OverlyCoupledClass"})
public final class Resource extends AbstractToString implements Serialisable, MapSerialisable
{
	/*
		"content_length": "19968",
		"openness_score": "0",
		"openness_score_failure_count": "1",
		"openness_score_reason": "The format entered for the resource doesn't match the description from the web server",
		"content_type": "application/vnd.ms-excel",
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
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String contentLengthField = "content_length";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String opennessScoreField = "openness_score";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String opennessScoreFailureCountField = "openness_score_failure_count";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String opennessScoreReasonField = "openness_score_reason";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String contentTypeField = "content_type";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String hubIdField = "hub-id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String resourceLocatorProtocolField = "resource_locator_protocol";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String resourceLocatorFunctionField = "resource_locator_function";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String dateField = "date";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String scraperUrlField = "scraper_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String verifiedDateField = "verified_date";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String verifiedField = "verified";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String ckanRecommendedWmsPreviewField = "ckan_recommended_wms_preview";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String scrapedField = "scraped";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String scraperSourceField = "scraper_source";

	@NotNull public final ResourceGroupId resourceGroupId;
	@Nullable public final MicrosecondTimestamp cacheLastUpdated;
	@NotNull public final PackageId packageId;
	@Nullable public final MicrosecondTimestamp webstoreLastUpdated;
	@Nullable public final Boolean datastoreActive;
	@NotNull public final ResourceId id;
	public final long size;
	@Nullable @NonNls public final String cacheFilePath;
	@Nullable public final MicrosecondTimestamp lastModified;
	@NotNull public final Hash hash;
	@NotNull @NonNls public final String description;
	@NotNull public final Format format;
	@NotNull public final TrackingSummary trackingSummary;
	@Nullable @NonNls public final String mimeTypeInner;
	@Nullable @NonNls public final String mimeType;
	@NotNull public final Url cacheUrl;
	@Nullable @NonNls public final String name;
	@Nullable public final MicrosecondTimestamp created;
	@NotNull public final Url url;
	@Nullable public final Url webstoreUrl;
	public final long position;
	@Nullable public final ResourceType resourceType;
	private final long contentLength;
	private final long opennessScore;
	private final long opennessScoreFailureCount;
	@Nullable @NonNls private final String opennessScoreReason;
	@Nullable @NonNls private final String contentType;
	@Nullable private final HubId hubId;
	@Nullable @NonNls private final String resourceLocatorProtocol;
	@Nullable @NonNls private final String resourceLocatorFunction;
	@Nullable private final Date date;
	@Nullable private final Url scraperUrl;
	@Nullable public final MicrosecondTimestamp verifiedDate;
	@Nullable public final Boolean verified;
	@Nullable public final Boolean ckanRecommendedWmsPreview;
	@Nullable private final Date scraped;
	@Nullable private final Url scraperSource;

	@SuppressWarnings({"ConstructorWithTooManyParameters", "OverlyCoupledMethod", "OverlyLongMethod"})
	public Resource(@NotNull final ResourceGroupId resourceGroupId, @Nullable final MicrosecondTimestamp cacheLastUpdated, @NotNull final PackageId packageId, @Nullable final MicrosecondTimestamp webstoreLastUpdated, @Nullable final Boolean datastoreActive, @NotNull final ResourceId id, final long size, @Nullable @NonNls final String cacheFilePath, @Nullable final MicrosecondTimestamp lastModified, @NotNull final Hash hash, @NotNull @NonNls final String description, @NotNull final Format format, @NotNull final TrackingSummary trackingSummary, @Nullable @NonNls final String mimeTypeInner, @Nullable @NonNls final String mimeType, @NotNull final Url cacheUrl, @Nullable @NonNls final String name, @Nullable final MicrosecondTimestamp created, @NotNull final Url url, @Nullable final Url webstoreUrl, final long position, @Nullable final ResourceType resourceType, final long contentLength, final long opennessScore, final long opennessScoreFailureCount, @Nullable @NonNls final String opennessScoreReason, @Nullable @NonNls final String contentType, @Nullable final HubId hubId, @Nullable @NonNls final String resourceLocatorProtocol, @Nullable @NonNls final String resourceLocatorFunction, @Nullable final Date date, @Nullable final Url scraperUrl, @Nullable final MicrosecondTimestamp verifiedDate, @Nullable final Boolean verified, @Nullable final Boolean ckanRecommendedWmsPreview, @Nullable final Date scraped, @Nullable final Url scraperSource)
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
		this.contentLength = contentLength;
		this.opennessScore = opennessScore;
		this.opennessScoreFailureCount = opennessScoreFailureCount;
		this.opennessScoreReason = opennessScoreReason;
		this.contentType = contentType;
		this.hubId = hubId;
		this.resourceLocatorProtocol = resourceLocatorProtocol;
		this.resourceLocatorFunction = resourceLocatorFunction;
		this.date = date;
		this.scraperUrl = scraperUrl;
		this.verifiedDate = verifiedDate;
		this.verified = verified;
		this.ckanRecommendedWmsPreview = ckanRecommendedWmsPreview;
		this.scraped = scraped;
		this.scraperSource = scraperSource;
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

	@SuppressWarnings({"FeatureEnvy", "OverlyLongMethod"})
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(resourceGroupIdField, resourceGroupId);
			writeNullableProperty(mapSerialiser, cacheLastUpdatedField, cacheLastUpdated);
			mapSerialiser.writeProperty(packageIdField, packageId);
			writeNullableProperty(mapSerialiser, webstoreLastUpdatedField, webstoreLastUpdated);
			writeNullableProperty(mapSerialiser, datastoreActiveField, datastoreActive);
			mapSerialiser.writeProperty(idField, id);
			writeNullableProperty(mapSerialiser, cacheFilePathField, cacheFilePath);
			writeNullableProperty(mapSerialiser, lastModifiedField, lastModified);
			mapSerialiser.writeProperty(hashField, hash);
			mapSerialiser.writeProperty(descriptionField, description);
			mapSerialiser.writeProperty(formatField, format);
			mapSerialiser.writeProperty(trackingSummaryField, trackingSummary);
			writeNullableProperty(mapSerialiser, mimeTypeInnerField, mimeTypeInner);
			writeNullableProperty(mapSerialiser, mimeTypeField, mimeType);
			mapSerialiser.writeProperty(cacheUrlField, cacheUrl);
			writeNullableProperty(mapSerialiser, nameField, name);
			writeNullableProperty(mapSerialiser, createdField, created);
			mapSerialiser.writeProperty(urlField, url);
			writeNullableProperty(mapSerialiser, webstoreUrlField, webstoreUrl);
			mapSerialiser.writeProperty(positionField, position);
			writeNullableProperty(mapSerialiser, resourceTypeField, resourceType);
			writeNullableProperty(mapSerialiser, contentLengthField, contentLength);
			writeNullableProperty(mapSerialiser, opennessScoreField, opennessScore);
			writeNullableProperty(mapSerialiser, opennessScoreFailureCountField, opennessScoreFailureCount);
			writeNullableProperty(mapSerialiser, opennessScoreReasonField, opennessScoreReason);
			writeNullableProperty(mapSerialiser, contentTypeField, contentType);
			writeNullableProperty(mapSerialiser, hubIdField, hubId);
			writeNullableProperty(mapSerialiser, resourceLocatorProtocolField, resourceLocatorProtocol);
			writeNullableProperty(mapSerialiser, resourceLocatorFunctionField, resourceLocatorFunction);
			writeNullableProperty(mapSerialiser, dateField, date);
			writeNullableProperty(mapSerialiser, scraperUrlField, scraperUrl);
			writeNullableProperty(mapSerialiser, verifiedDateField, verifiedDate);
			writeNullableProperty(mapSerialiser, verifiedField, verified);
			writeNullableProperty(mapSerialiser, ckanRecommendedWmsPreviewField, ckanRecommendedWmsPreview);
			writeNullableProperty(mapSerialiser, scrapedField, scraped);
			writeNullableProperty(mapSerialiser, scraperSourceField, scraperSource);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"ConditionalExpression", "FeatureEnvy", "OverlyComplexMethod", "OverlyLongMethod"})
	@Override
	public boolean equals(final Object obj)
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

		if (contentLength != resource.contentLength)
		{
			return false;
		}
		if (opennessScore != resource.opennessScore)
		{
			return false;
		}
		if (opennessScoreFailureCount != resource.opennessScoreFailureCount)
		{
			return false;
		}
		if (position != resource.position)
		{
			return false;
		}
		if (size != resource.size)
		{
			return false;
		}
		if (cacheFilePath != null ? !cacheFilePath.equals(resource.cacheFilePath) : resource.cacheFilePath != null)
		{
			return false;
		}
		if (cacheLastUpdated != null ? !cacheLastUpdated.equals(resource.cacheLastUpdated) : resource.cacheLastUpdated != null)
		{
			return false;
		}
		if (!cacheUrl.equals(resource.cacheUrl))
		{
			return false;
		}
		if (contentType != null ? !contentType.equals(resource.contentType) : resource.contentType != null)
		{
			return false;
		}
		if (created != null ? !created.equals(resource.created) : resource.created != null)
		{
			return false;
		}
		if (datastoreActive != null ? !datastoreActive.equals(resource.datastoreActive) : resource.datastoreActive != null)
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
		if (lastModified != null ? !lastModified.equals(resource.lastModified) : resource.lastModified != null)
		{
			return false;
		}
		if (mimeType != null ? !mimeType.equals(resource.mimeType) : resource.mimeType != null)
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
		if (hubId != null ? !hubId.equals(resource.hubId) : resource.hubId != null)
		{
			return false;
		}
		if (resourceLocatorProtocol != null ? !resourceLocatorProtocol.equals(resource.resourceLocatorProtocol) : resource.resourceLocatorProtocol != null)
		{
			return false;
		}
		if (resourceLocatorFunction != null ? !resourceLocatorFunction.equals(resource.resourceLocatorFunction) : resource.resourceLocatorFunction != null)
		{
			return false;
		}
		if (opennessScoreReason != null ? !opennessScoreReason.equals(resource.opennessScoreReason) : resource.opennessScoreReason != null)
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
		if (date != null ? !date.equals(resource.date) : resource.date != null)
		{
			return false;
		}
		if (scraperUrl != null ? !scraperUrl.equals(resource.scraperUrl) : resource.scraperUrl != null)
		{
			return false;
		}
		if (verifiedDate != null ? !verifiedDate.equals(resource.verifiedDate) : resource.verifiedDate != null)
		{
			return false;
		}
		if (verified != null ? !verified.equals(resource.verified) : resource.verified != null)
		{
			return false;
		}
		if (ckanRecommendedWmsPreview != null ? !ckanRecommendedWmsPreview.equals(resource.ckanRecommendedWmsPreview) : resource.ckanRecommendedWmsPreview != null)
		{
			return false;
		}
		if (scraped != null ? !scraped.equals(resource.scraped) : resource.scraped != null)
		{
			return false;
		}
		if (scraperSource != null ? !scraperSource.equals(resource.scraperSource) : resource.scraperSource != null)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"ConditionalExpression", "MethodWithMoreThanThreeNegations", "FeatureEnvy", "ConstantConditions", "OverlyComplexMethod", "OverlyLongMethod"})
	@Override
	public int hashCode()
	{
		int result = resourceGroupId.hashCode();
		result = 31 * result + (cacheLastUpdated != null ? cacheLastUpdated.hashCode() : 0);
		result = 31 * result + packageId.hashCode();
		result = 31 * result + (webstoreLastUpdated != null ? webstoreLastUpdated.hashCode() : 0);
		result = 31 * result + (datastoreActive != null ? datastoreActive.hashCode() : 0);
		result = 31 * result + id.hashCode();
		result = 31 * result + (int) (size ^ (size >>> 32));
		result = 31 * result + (cacheFilePath != null ? cacheFilePath.hashCode() : 0);
		result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
		result = 31 * result + hash.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + format.hashCode();
		result = 31 * result + trackingSummary.hashCode();
		result = 31 * result + (mimeTypeInner != null ? mimeTypeInner.hashCode() : 0);
		result = 31 * result + (hubId != null ? hubId.hashCode() : 0);
		result = 31 * result + (resourceLocatorProtocol != null ? resourceLocatorProtocol.hashCode() : 0);
		result = 31 * result + (resourceLocatorFunction != null ? resourceLocatorFunction.hashCode() : 0);
		result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
		result = 31 * result + cacheUrl.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		result = 31 * result + url.hashCode();
		result = 31 * result + (webstoreUrl != null ? webstoreUrl.hashCode() : 0);
		result = 31 * result + (int) (position ^ (position >>> 32));
		result = 31 * result + (resourceType != null ? resourceType.hashCode() : 0);
		result = 31 * result + (int) (contentLength ^ (contentLength >>> 32));
		result = 31 * result + (int) (opennessScore ^ (opennessScore >>> 32));
		result = 31 * result + (int) (opennessScoreFailureCount ^ (opennessScoreFailureCount >>> 32));
		result = 31 * result + (opennessScoreReason != null ? opennessScoreReason.hashCode() : 0);
		result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (scraperUrl != null ? scraperUrl.hashCode() : 0);
		result = 31 * result + (verifiedDate != null ? verifiedDate.hashCode() : 0);
		result = 31 * result + (verified != null ? verified.hashCode() : 0);
		result = 31 * result + (ckanRecommendedWmsPreview != null ? ckanRecommendedWmsPreview.hashCode() : 0);
		result = 31 * result + (scraped != null ? scraped.hashCode() : 0);
		result = 31 * result + (scraperSource != null ? scraperSource.hashCode() : 0);
		return result;
	}
}
