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

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;

import static java.util.Arrays.copyOf;

public final class ManySearchCriteria<T> extends AbstractToString implements SearchCriteria<T>
{
	@SuppressWarnings("unchecked") @NotNull private static final SearchCriteria<?> None = new ManySearchCriteria();

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T> SearchCriteria<T> noSearchCriteria()
	{
		return (SearchCriteria<T>) None;
	}

	@NotNull private final SearchCriterion<T>[] searchCriteria;
	private final int length;

	@SafeVarargs
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public ManySearchCriteria(@NotNull final SearchCriterion<T>... searchCriteria)
	{
		this.searchCriteria = searchCriteria;
		length = searchCriteria.length;
	}

	@Override
	@NotNull
	public SearchCriteria<T> and(@NotNull final SearchCriterion<T> searchCriterion)
	{
		final SearchCriterion<T>[] copy = copyOf(searchCriteria, length + 1);
		copy[length] = searchCriterion;
		return new ManySearchCriteria<>(copy);
	}

	@Override
	@NotNull
	public Pair<String, String>[] toUnencodedQueryStringParameters()
	{
		@SuppressWarnings("unchecked") final Pair<String, String>[] queryStringParameters = new Pair[length];
		for (int index = 0; index < length; index++)
		{
			final SearchCriterion<T> searchCriterion = searchCriteria[index];
			queryStringParameters[index] = searchCriterion.toUnencodedQueryStringParameterKeyValuePair();
		}
		return queryStringParameters;
	}
}
