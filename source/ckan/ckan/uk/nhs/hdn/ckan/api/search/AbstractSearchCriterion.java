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
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.FieldTokenName;
import uk.nhs.hdn.common.tuples.Pair;

public abstract class AbstractSearchCriterion<T> extends AbstractToString implements SearchCriterion<T>
{
	@NotNull private final Class<T> taint;
	@NotNull @NonNls @FieldTokenName private final String key;

	protected AbstractSearchCriterion(@NotNull final Class<T> taint, @NotNull @FieldTokenName @NonNls final String key)
	{
		this.taint = taint;
		this.key = key;
	}

	@NotNull
	@Override
	public final SearchCriteria<T> and(@NotNull final SearchCriterion<T> searchCriterion)
	{
		return new ManySearchCriteria<>(this, searchCriterion);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public Pair<String, String>[] toUnencodedQueryStringParameters()
	{
		return new Pair[]{toUnencodedQueryStringParameterKeyValuePair()};
	}

	@NotNull
	protected final Pair<String, String> toUnencodedQueryStringParameters(@NotNull final String value)
	{
		return new Pair<>(key, value);
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

		final AbstractSearchCriterion<?> that = (AbstractSearchCriterion<?>) obj;

		if (!key.equals(that.key))
		{
			return false;
		}
		if (!taint.equals(that.taint))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = taint.hashCode();
		result = 31 * result + key.hashCode();
		return result;
	}
}
