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

package uk.nhs.hdn.ckan.api.search;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.Dataset;
import uk.nhs.hdn.ckan.domain.Resource;
import uk.nhs.hdn.common.serialisers.FieldTokenName;
import uk.nhs.hdn.common.tuples.Pair;

import static uk.nhs.hdn.ckan.domain.Dataset.*;
import static uk.nhs.hdn.ckan.domain.Resource.*;

public class StringSearchCriterion<T> extends AbstractSearchCriterion<T>
{
	@NotNull
	public static SearchCriterion<Dataset> datasetAnySearchCriterion(@NotNull @NonNls final String caseInsensitiveSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, "q", caseInsensitiveSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetTitleSearchCriterion(@NotNull @NonNls final String caseInsensitiveTitleSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, titleField, caseInsensitiveTitleSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetTagsSearchCriterion(@NotNull @NonNls final String caseInsensitiveTagSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, tagsField, caseInsensitiveTagSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetNotesSearchCriterion(@NotNull @NonNls final String caseInsensitiveNotesSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, notesField, caseInsensitiveNotesSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetGroupsSearchCriterion(@NotNull @NonNls final String caseInsensitiveGroupsSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, groupsField, caseInsensitiveGroupsSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetAuthorSearchCriterion(@NotNull @NonNls final String caseInsensitiveAuthorSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, authorField, caseInsensitiveAuthorSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetMaintainerSearchCriterion(@NotNull @NonNls final String caseInsensitiveMaintainerSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, maintainerField, caseInsensitiveMaintainerSubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetUpdateFrequencySearchCriterion(@NotNull @NonNls final String caseInsensitiveUpdateFrequencySubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, "update_frequency", caseInsensitiveUpdateFrequencySubstring);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetSearchCriterion(@NotNull @FieldTokenName @NonNls final String key, @NotNull @NonNls final String caseInsensitiveSubstring)
	{
		return new StringSearchCriterion<>(Dataset.class, key, caseInsensitiveSubstring);
	}

	@NotNull
	public static SearchCriterion<Resource> resourceUrlSearchCriterion(@NotNull @NonNls final String caseInsensitiveUrlSubstring)
	{
		return new StringSearchCriterion<>(Resource.class, Resource.urlField, caseInsensitiveUrlSubstring);
	}

	@NotNull
	public static SearchCriterion<Resource> resourceDescriptionSearchCriterion(@NotNull @NonNls final String caseInsensitiveDescriptionSubstring)
	{
		return new StringSearchCriterion<>(Resource.class, descriptionField, caseInsensitiveDescriptionSubstring);
	}

	@NotNull
	public static SearchCriterion<Resource> resourceFormatSearchCriterion(@NotNull @NonNls final String caseInsensitiveFormatSubstring)
	{
		return new StringSearchCriterion<>(Resource.class, formatField, caseInsensitiveFormatSubstring);
	}

	@NotNull @NonNls private final String value;

	public StringSearchCriterion(@NotNull final Class<T> taint, @NotNull @FieldTokenName @NonNls final String key, @NotNull @NonNls final String value)
	{
		super(taint, key);
		this.value = value;
	}

	@Override
	public final boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		if (!super.equals(obj))
		{
			return false;
		}

		final StringSearchCriterion<?> that = (StringSearchCriterion<?>) obj;

		if (!value.equals(that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	@NotNull
	@Override
	public final Pair<String, String> toUnencodedQueryStringParameterKeyValuePair()
	{
		return toUnencodedQueryStringParameters(value);
	}
}
