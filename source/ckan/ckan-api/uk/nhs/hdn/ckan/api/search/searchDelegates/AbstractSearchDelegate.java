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

package uk.nhs.hdn.ckan.api.search.searchDelegates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.domain.SearchResult;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.arraycopy;
import static uk.nhs.hdn.ckan.api.search.UnsignedLongSearchCriterion.MaximumLimit;
import static uk.nhs.hdn.ckan.api.search.UnsignedLongSearchCriterion.MinimumOffset;

public abstract class AbstractSearchDelegate<S extends ValueSerialisable, T> extends AbstractToString implements SearchDelegate<S, T>
{
	@NotNull private final ArrayCreator<S> arrayCreator;

	protected AbstractSearchDelegate(@NotNull final ArrayCreator<S> arrayCreator)
	{
		this.arrayCreator = arrayCreator;
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

		final AbstractSearchDelegate<?, ?> that = (AbstractSearchDelegate<?, ?>) obj;

		if (!arrayCreator.equals(that.arrayCreator))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return arrayCreator.hashCode();
	}

	@Override
	@NotNull
	public final S[] allResults(@NotNull final SearchCriteria<T> searchCriteria) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final List<S[]> allResults = new ArrayList<>(10);
		long offset = MinimumOffset;
		long count;
		do
		{
			final SearchResult<S> searchResult = search(searchCriteria, offset, MaximumLimit).get();
			final S[] results = searchResult.results;
			count = searchResult.count;
			final long length = (long) results.length;
			allResults.add(results);
			offset += length;
		}
		while (offset < count);

		if (allResults.size() == 1)
		{
			return allResults.get(0);
		}

		@SuppressWarnings("NumericCastThatLosesPrecision") final S[] result = arrayCreator.newInstance1((int) count);
		int destinationPosition = 0;
		for (final S[] allResult : allResults)
		{
			final int length = allResult.length;
			arraycopy(allResult, 0, result, destinationPosition, length);
			destinationPosition += length;
		}
		return result;
	}
}
