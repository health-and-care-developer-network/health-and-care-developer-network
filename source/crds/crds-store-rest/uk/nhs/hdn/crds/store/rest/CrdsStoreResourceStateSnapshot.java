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

package uk.nhs.hdn.crds.store.rest;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.NotFoundException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.AbstractWithSubResourcesResourceStateSnapshot;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.SubResource;

import static uk.nhs.hdn.common.GregorianCalendarHelper.utcNow;

public final class CrdsStoreResourceStateSnapshot extends AbstractWithSubResourcesResourceStateSnapshot
{
	// It may be possible to use something hanging off the map...
	// TODO: Incorrect, causes caching to not happen
	public CrdsStoreResourceStateSnapshot()
	{
		super(utcNow());
	}

	@NotNull
	@Override
	public SubResource find(@NotNull final String rawPath) throws NotFoundException
	{
		if (rawPath.isEmpty())
		{
			return allTuplesSubResource;
		}
		if (rawPath.contains("/"))
		{
			throw new NotFoundException("/ is not used");
		}
		return barcodeSubResource(rawPath);
	}

	/*
		Some sort of LRU cache
			- weak references to allow GC;
			- notifications to re-cache (actually, just clear)
			- concurrent
			- keys are paths


	 */

	public interface SubResourceCache
	{
		@NotNull
		SubResource get(@NotNull final String rawPath);
	}
}
