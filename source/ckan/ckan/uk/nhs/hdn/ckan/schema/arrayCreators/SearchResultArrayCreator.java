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

package uk.nhs.hdn.ckan.schema.arrayCreators;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.SearchResult;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;

public final class SearchResultArrayCreator<S extends ValueSerialisable> extends AbstractArrayCreator<SearchResult<S>>
{
	@NotNull public static final ArrayCreator<SearchResult<ResourceId>> SearchResultResourceIdArray = new SearchResultArrayCreator<>();
	@NotNull public static final ArrayCreator<SearchResult<DatasetId>> SearchResultDatasetIdArray = new SearchResultArrayCreator<>();
	@NotNull public static final ArrayCreator<SearchResult<DatasetName>> SearchResultDatasetNameArray = new SearchResultArrayCreator<>();

	private SearchResultArrayCreator()
	{
		super(SearchResultArrayCreator.<S>getType(), SearchResultArrayCreator.<S>getArrayType());
	}

	@SuppressWarnings("unchecked")
	private static <S extends ValueSerialisable> Class<SearchResult<S>> getType()
	{
		final Object searchResultClass = SearchResult.class;
		return (Class<SearchResult<S>>) searchResultClass;
	}

	@SuppressWarnings("unchecked")
	private static <S extends ValueSerialisable> Class<SearchResult<S>[]> getArrayType()
	{
		@SuppressWarnings("rawtypes") final Object arrayClass = SearchResult[].class;
		return (Class<SearchResult<S>[]>) arrayClass;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public SearchResult<S>[] newInstance1(final int size)
	{
		return new SearchResult[size];
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public SearchResult<S>[][] newInstance2(final int size)
	{
		return new SearchResult[size][];
	}
}
