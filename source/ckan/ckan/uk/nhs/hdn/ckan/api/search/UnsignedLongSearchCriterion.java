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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public class UnsignedLongSearchCriterion<T> extends AbstractSearchCriterion<T>
{
	public static final long MinimumOffset = 0L;
	public static final long MaximumLimit = 1000L;

	@NotNull
	public static SearchCriterion<Resource> resourceLimitSearchCriterion(final long limit)
	{
		return limitSearchCriterion(Resource.class, limit);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetLimitSearchCriterion(final long limit)
	{
		return limitSearchCriterion(Dataset.class, limit);
	}

	@NotNull
	public static <T> SearchCriterion<T> limitSearchCriterion(@NotNull final Class<T> taint, final long limit)
	{
		return new UnsignedLongSearchCriterion<>(taint, "limit", limit);
	}

	@NotNull
	public static SearchCriterion<Resource> resourceOffsetSearchCriterion(final long offset)
	{
		return offsetSearchCriterion(Resource.class, offset);
	}

	@NotNull
	public static SearchCriterion<Dataset> datasetOffsetSearchCriterion(final long offset)
	{
		return offsetSearchCriterion(Dataset.class, offset);
	}

	@NotNull
	public static <T> SearchCriterion<T> offsetSearchCriterion(@NotNull final Class<T> taint, final long offset)
	{
		return new UnsignedLongSearchCriterion<>(taint, "offset", offset);
	}

	private final long value;

	public UnsignedLongSearchCriterion(@NotNull final Class<T> taint, @NotNull @FieldTokenName @NonNls final String key, final long value)
	{
		super(taint, key);
		if (value < 0L)
		{
			throw new IllegalArgumentException(format(ENGLISH, "value, %1$s, can not be negative", value));
		}
		this.value = value;
	}

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
		if (!super.equals(obj))
		{
			return false;
		}

		final UnsignedLongSearchCriterion<?> that = (UnsignedLongSearchCriterion<?>) obj;

		if (value != that.value)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (int) (value ^ (value >>> 32));
		return result;
	}

	@NotNull
	@Override
	public final Pair<String, String> toUnencodedQueryStringParameterKeyValuePair()
	{
		return toUnencodedQueryStringParameters(Long.toString(value));
	}
}
