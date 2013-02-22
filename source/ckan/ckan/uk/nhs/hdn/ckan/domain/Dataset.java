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
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.enumerations.State;
import uk.nhs.hdn.ckan.domain.enumerations.Type;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.ckan.domain.uniqueNames.LicenceId;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.ckan.domain.urls.Url;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;

import java.util.Arrays;
import java.util.Map;

import static uk.nhs.hdn.ckan.domain.TrackingSummary.recentField;
import static uk.nhs.hdn.ckan.domain.TrackingSummary.totalField;
import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writeNullableProperty;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.IgnoreChildrenMatcher.ignoreChildren;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.recurse;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

@SuppressWarnings({"ClassWithTooManyFields", "OverlyCoupledClass"})
public final class Dataset extends AbstractToString implements Serialisable, MapSerialisable
{
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String licenceTitleField = "license_title";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String maintainerField = "maintainer";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String maintainerEmailField = "maintainer_email";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String idField = "id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String metadataCreatedField = "metadata_created";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String relationshipsField = "relationships";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String licenceField = "license";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String metadataModifiedField = "metadata_modified";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String authorField = "author";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String authorEmailField = "author_email";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String stateField = "state";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String versionField = "version";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String licenceIdField = "license_id";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String typeField = "type";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String resourcesField = "resources";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String tagsField = "tags";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String trackingSummaryField = "tracking_summary";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String groupsField = "groups";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String nameField = "name";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String isOpenField = "isopen";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String notesRenderedField = "notes_rendered";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String urlField = "url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String ckanUrlField = "ckan_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String notesField = "notes";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String titleField = "title";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String ratingsAverageField = "ratings_average";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String extrasField = "extras";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String licenceUrlField = "license_url";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String ratingsCountField = "ratings_count";
	@SuppressWarnings("ConstantNamingConvention") @FieldTokenName @NonNls @NotNull public static final String revisionIdField = "revision_id";

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		licenceTitleField,
		maintainerField,
		maintainerEmailField,
		idField,
		metadataCreatedField,
		relationshipsField,
		licenceField,
		metadataModifiedField,
		authorField,
		authorEmailField,
		stateField,
		versionField,
		licenceIdField,
		typeField,
		resourcesField,
		tagsField,
		trackingSummaryField + totalField,
		trackingSummaryField + recentField,
		groupsField,
		nameField,
		isOpenField,
		notesRenderedField,
		urlField,
		ckanUrlField,
		notesField,
		titleField,
		ratingsAverageField,
		extrasField,
		licenceUrlField,
		ratingsCountField,
		revisionIdField
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(licenceTitleField, 0),
		leaf(maintainerField, 1),
		leaf(maintainerEmailField, 2),
		leaf(idField, 3),
		leaf(metadataCreatedField, 4),
		leaf(relationshipsField, 5),
		leaf(licenceField, 6),
		leaf(metadataModifiedField, 7),
		leaf(authorField, 8),
		leaf(authorEmailField, 9),
		leaf(stateField, 10),
		leaf(versionField, 11),
		leaf(licenceIdField, 12),
		leaf(typeField, 13),
		leaf(resourcesField, 14),
		leaf(tagsField, 15),
		recurse(trackingSummaryField,
			leaf(totalField, 16),
			leaf(recentField, 17)
		),
		leaf(groupsField, 18),
		leaf(nameField, 19),
		leaf(isOpenField, 20),
		leaf(notesRenderedField, 21),
		leaf(urlField, 22),
		leaf(ckanUrlField, 23),
		leaf(notesField, 24),
		leaf(titleField, 25),
		leaf(ratingsAverageField, 26),
		ignoreChildren(extrasField, 27),
		leaf(licenceUrlField, 28),
		leaf(ratingsCountField, 29),
		leaf(revisionIdField, 30)
	);

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForDatasets(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@SuppressWarnings("ConditionalExpression")
	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForDatasets()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	@NonNls @Nullable public final String licenceTitle;
	@NonNls @Nullable public final String maintainer;
	@NonNls @Nullable public final String maintainerEmail;
	@NotNull public final DatasetId id;
	@NotNull public final MicrosecondTimestamp metadataCreated;
	@NotNull public final DatasetName[] relationships; // broken!
	@NonNls @Nullable public final String licence;
	@NotNull public final MicrosecondTimestamp metadataModified;
	@NonNls @Nullable public final String author;
	@NonNls @Nullable public final String authorEmail;
	@NotNull public final State state;
	@NonNls @Nullable public final String version;
	@Nullable public final LicenceId licenceId;
	@Nullable public final Type type;
	@NotNull public final Resource[] resources;
	@NotNull public final TagName[] tags;
	@NotNull public final TrackingSummary trackingSummary;
	@NotNull public final GroupId[] groups;
	@NotNull public final DatasetName name;
	public final boolean isOpen;
	@NonNls @NotNull public final String notesRendered;
	@NotNull public final Url url;
	@NotNull public final Url ckanUrl;
	@NonNls @Nullable public final String notes;
	@NonNls @NotNull public final String title;
	@NonNls @Nullable public final String ratingsAverage;
	@NotNull public final Map<String, Object> extras;
	@NotNull public final Url licenceUrl;
	public final long ratingsCount;
	@NotNull public final RevisionId revisionId;

	@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter", "ConstructorWithTooManyParameters", "OverlyCoupledMethod"})
	public Dataset(@Nullable @NonNls final String licenceTitle, @Nullable @NonNls final String maintainer, @Nullable @NonNls final String maintainerEmail, @NotNull final DatasetId id, @NotNull final MicrosecondTimestamp metadataCreated, @NotNull final DatasetName[] relationships, @Nullable @NonNls final String licence, @NotNull final MicrosecondTimestamp metadataModified, @Nullable @NonNls final String author, @Nullable @NonNls final String authorEmail, @NotNull final State state, @Nullable @NonNls final String version, @Nullable final LicenceId licenceId, @Nullable final Type type, @NotNull final Resource[] resources, @NotNull final TagName[] tags, @NotNull final TrackingSummary trackingSummary, @NotNull final GroupId[] groups, @NotNull final DatasetName name, final boolean isOpen, @NotNull @NonNls final String notesRendered, @NotNull final Url url, @NotNull final Url ckanUrl, @Nullable @NonNls final String notes, @NotNull @NonNls final String title, @Nullable @NonNls final String ratingsAverage, @NotNull final Map<String, Object> extras, @NotNull final Url licenceUrl, final long ratingsCount, @NotNull final RevisionId revisionId)
	{
		this.licenceTitle = licenceTitle;
		this.maintainer = maintainer;
		this.maintainerEmail = maintainerEmail;
		this.id = id;
		this.metadataCreated = metadataCreated;
		this.relationships = relationships;
		this.licence = licence;
		this.metadataModified = metadataModified;
		this.author = author;
		this.authorEmail = authorEmail;
		this.state = state;
		this.version = version;
		this.licenceId = licenceId;
		this.type = type;
		this.resources = resources;
		this.tags = tags;
		this.trackingSummary = trackingSummary;
		this.groups = groups;
		this.name = name;
		this.isOpen = isOpen;
		this.notesRendered = notesRendered;
		this.url = url;
		this.ckanUrl = ckanUrl;
		this.notes = notes;
		this.title = title;
		this.ratingsAverage = ratingsAverage;
		this.extras = extras;
		this.licenceUrl = licenceUrl;
		this.ratingsCount = ratingsCount;
		this.revisionId = revisionId;
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
			writeNullableProperty(mapSerialiser, licenceTitleField, licenceTitle);
			writeNullableProperty(mapSerialiser, maintainerField, maintainer);
			writeNullableProperty(mapSerialiser, maintainerEmailField, maintainerEmail);
			mapSerialiser.writeProperty(idField, id);
			mapSerialiser.writeProperty(metadataCreatedField, metadataCreated);
			mapSerialiser.writeProperty(relationshipsField, relationships);
			writeNullableProperty(mapSerialiser, licenceField, licence);
			mapSerialiser.writeProperty(metadataModifiedField, metadataModified);
			writeNullableProperty(mapSerialiser, authorField, author);
			writeNullableProperty(mapSerialiser, authorEmailField, authorEmail);
			mapSerialiser.writeProperty(stateField, state);
			writeNullableProperty(mapSerialiser, versionField, version);
			writeNullableProperty(mapSerialiser, licenceIdField, licenceId);
			writeNullableProperty(mapSerialiser, typeField, type);
			mapSerialiser.writeProperty(resourcesField, resources);
			mapSerialiser.writeProperty(tagsField, tags);
			mapSerialiser.writeProperty(trackingSummaryField, trackingSummary);
			mapSerialiser.writeProperty(groupsField, groups);
			mapSerialiser.writeProperty(nameField, name);
			mapSerialiser.writeProperty(isOpenField, isOpen);
			mapSerialiser.writeProperty(notesRenderedField, notesRendered);
			mapSerialiser.writeProperty(urlField, url);
			mapSerialiser.writeProperty(ckanUrlField, ckanUrl);
			writeNullableProperty(mapSerialiser, notesField, notes);
			mapSerialiser.writeProperty(titleField, title);
			writeNullableProperty(mapSerialiser, ratingsAverageField, ratingsAverage);
			mapSerialiser.writeProperty(extrasField, extras);
			mapSerialiser.writeProperty(licenceUrlField, licenceUrl);
			mapSerialiser.writeProperty(ratingsCountField, ratingsCount);
			mapSerialiser.writeProperty(revisionIdField, revisionId);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "ConditionalExpression"})
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

		final Dataset dataset = (Dataset) obj;

		if (isOpen != dataset.isOpen)
		{
			return false;
		}
		if (ratingsCount != dataset.ratingsCount)
		{
			return false;
		}
		if (author != null ? !author.equals(dataset.author) : dataset.author != null)
		{
			return false;
		}
		if (authorEmail != null ? !authorEmail.equals(dataset.authorEmail) : dataset.authorEmail != null)
		{
			return false;
		}
		if (!ckanUrl.equals(dataset.ckanUrl))
		{
			return false;
		}
		if (!extras.equals(dataset.extras))
		{
			return false;
		}
		if (!Arrays.equals(groups, dataset.groups))
		{
			return false;
		}
		if (!id.equals(dataset.id))
		{
			return false;
		}
		if (!name.equals(dataset.name))
		{
			return false;
		}
		if (licence != null ? !licence.equals(dataset.licence) : dataset.licence != null)
		{
			return false;
		}
		if (licenceId != null ? !licenceId.equals(dataset.licenceId) : dataset.licenceId != null)
		{
			return false;
		}
		if (licenceTitle != null ? !licenceTitle.equals(dataset.licenceTitle) : dataset.licenceTitle != null)
		{
			return false;
		}
		if (!licenceUrl.equals(dataset.licenceUrl))
		{
			return false;
		}
		if (maintainer != null ? !maintainer.equals(dataset.maintainer) : dataset.maintainer != null)
		{
			return false;
		}
		if (maintainerEmail != null ? !maintainerEmail.equals(dataset.maintainerEmail) : dataset.maintainerEmail != null)
		{
			return false;
		}
		if (!metadataCreated.equals(dataset.metadataCreated))
		{
			return false;
		}
		if (!metadataModified.equals(dataset.metadataModified))
		{
			return false;
		}
		if (notes != null ? !notes.equals(dataset.notes) : dataset.notes != null)
		{
			return false;
		}
		if (!notesRendered.equals(dataset.notesRendered))
		{
			return false;
		}
		if (ratingsAverage != null ? !ratingsAverage.equals(dataset.ratingsAverage) : dataset.ratingsAverage != null)
		{
			return false;
		}
		if (!Arrays.equals(relationships, dataset.relationships))
		{
			return false;
		}
		if (!Arrays.equals(resources, dataset.resources))
		{
			return false;
		}
		if (!revisionId.equals(dataset.revisionId))
		{
			return false;
		}
		if (state != dataset.state)
		{
			return false;
		}
		if (!Arrays.equals(tags, dataset.tags))
		{
			return false;
		}
		if (!title.equals(dataset.title))
		{
			return false;
		}
		if (!trackingSummary.equals(dataset.trackingSummary))
		{
			return false;
		}
		if (type != dataset.type)
		{
			return false;
		}
		if (!url.equals(dataset.url))
		{
			return false;
		}
		if (version != null ? !version.equals(dataset.version) : dataset.version != null)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"MethodWithMoreThanThreeNegations", "ConditionalExpression", "OverlyLongMethod", "OverlyComplexMethod"})
	@Override
	public int hashCode()
	{
		int result = licenceTitle != null ? licenceTitle.hashCode() : 0;
		result = 31 * result + (maintainer != null ? maintainer.hashCode() : 0);
		result = 31 * result + (maintainerEmail != null ? maintainerEmail.hashCode() : 0);
		result = 31 * result + id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + metadataCreated.hashCode();
		result = 31 * result + Arrays.hashCode(relationships);
		result = 31 * result + (licence != null ? licence.hashCode() : 0);
		result = 31 * result + metadataModified.hashCode();
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (authorEmail != null ? authorEmail.hashCode() : 0);
		result = 31 * result + state.hashCode();
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (licenceId != null ? licenceId.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(resources);
		result = 31 * result + Arrays.hashCode(tags);
		result = 31 * result + trackingSummary.hashCode();
		result = 31 * result + Arrays.hashCode(groups);
		result = 31 * result + (isOpen ? 1 : 0);
		result = 31 * result + notesRendered.hashCode();
		result = 31 * result + url.hashCode();
		result = 31 * result + ckanUrl.hashCode();
		result = 31 * result + notes.hashCode();
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		result = 31 * result + title.hashCode();
		result = 31 * result + (ratingsAverage != null ? ratingsAverage.hashCode() : 0);
		result = 31 * result + extras.hashCode();
		result = 31 * result + licenceUrl.hashCode();
		result = 31 * result + (int) (ratingsCount ^ (ratingsCount >>> 32));
		result = 31 * result + revisionId.hashCode();
		return result;
	}
}
