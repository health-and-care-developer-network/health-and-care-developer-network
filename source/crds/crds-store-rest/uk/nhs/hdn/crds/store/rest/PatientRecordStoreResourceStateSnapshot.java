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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.caching.caching.Cache;
import uk.nhs.hdn.common.caching.caching.ThreadUnsafeLeastRecentlyAccessedCache;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.NotFoundException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.AbstractWithSubResourcesResourceStateSnapshot;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.SubResource;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.store.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.store.eventObservers.InvalidateCacheEventObserver;
import uk.nhs.hdn.crds.store.recordStore.RecordStore;
import uk.nhs.hdn.number.NhsNumber;

public final class PatientRecordStoreResourceStateSnapshot extends AbstractWithSubResourcesResourceStateSnapshot
{
	@NotNull private final ThreadLocal<Cache<NhsNumber,PatientRecordSubResource>> cacheThreadLocal;

	@NotNull
	public static ThreadLocal<Cache<NhsNumber, PatientRecordSubResource>> patientRecordCache(final int cacheMaximumNumberOfEntries, @NotNull final RecordStore<NhsNumber, SimplePatientRecord> patientRecordStore, @NotNull final ConcurrentAggregatedEventObserver<NhsNumber> concurrentAggregatedRepositoryEventObserver)
	{
		return new ThreadLocal<Cache<NhsNumber, PatientRecordSubResource>>()
		{
			@SuppressWarnings("RefusedBequest")
			@Override
			protected Cache<NhsNumber, PatientRecordSubResource> initialValue()
			{
				final ThreadUnsafeLeastRecentlyAccessedCache<NhsNumber, PatientRecordSubResource> cache = new ThreadUnsafeLeastRecentlyAccessedCache<>(cacheMaximumNumberOfEntries, new PatientRecordSubResourceSourceOfValuesToCache(patientRecordStore));
				concurrentAggregatedRepositoryEventObserver.add(new InvalidateCacheEventObserver<>(cache));
				return cache;
			}
		};
	}

	public PatientRecordStoreResourceStateSnapshot(final int cacheMaximumNumberOfEntries, @NotNull final RecordStore<NhsNumber, SimplePatientRecord> patientRecordStore, @NotNull final ConcurrentAggregatedEventObserver<NhsNumber> concurrentAggregatedRepositoryEventObserver)
	{
		cacheThreadLocal = patientRecordCache(cacheMaximumNumberOfEntries, patientRecordStore, concurrentAggregatedRepositoryEventObserver);
	}

	@NotNull
	@Override
	public SubResource find(@NotNull final String rawPath) throws NotFoundException
	{
		if (rawPath.isEmpty())
		{
			throw new NotFoundException("Iteration of patients is not supported");
		}
		if (rawPath.contains("/"))
		{
			throw new NotFoundException("/ is not used");
		}
		final NhsNumber nhsNumber;
		try
		{
			nhsNumber = NhsNumber.valueOf(rawPath);
		}
		catch(RuntimeException e)
		{
			throw new NotFoundException("Invalid NHS Number", e);
		}
		@Nullable final PatientRecordSubResource patientRecordSubResource = cacheThreadLocal.get().get(nhsNumber);
		if (patientRecordSubResource == null)
		{
			throw new NotFoundException("Unknown NHS Number");
		}
		return patientRecordSubResource;
	}

}
