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
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.domain.SearchResult;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;

public interface SearchDelegate<S extends ValueSerialisable, T>
{
	@NotNull
	S[] allResults(@NotNull final SearchCriteria<T> searchCriteria) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException;

	@NotNull
	ApiMethod<SearchResult<S>> search(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SearchCriteria<T> searchCriteria, final long offset, final long limit);
}
