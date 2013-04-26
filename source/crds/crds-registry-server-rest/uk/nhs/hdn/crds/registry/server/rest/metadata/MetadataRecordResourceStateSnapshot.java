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

package uk.nhs.hdn.crds.registry.server.rest.metadata;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.caching.caching.Cache;
import uk.nhs.hdn.common.caching.caching.ThreadUnsafeLeastRecentlyAccessedCache;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.NotFoundException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.AbstractWithSubResourcesResourceStateSnapshot;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.SubResource;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.crds.registry.server.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.registry.server.eventObservers.InvalidateCacheEventObserver;
import uk.nhs.hdn.crds.registry.recordStore.RecordStore;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Locale.UK;

public final class MetadataRecordResourceStateSnapshot extends AbstractWithSubResourcesResourceStateSnapshot
{
	@NotNull private final ThreadLocal<Cache<Identifier, MetadataRecordSubResource>> cacheThreadLocal;
	@NotNull @NonNls private final String iterationDisallowed;
	@NotNull @NonNls private final String unknownIdentifier;
	@NotNull private final IdentifierConstructor identifierConstructor;

	@NotNull
	public static ThreadLocal<Cache<Identifier, MetadataRecordSubResource>> identifierRecordCache(final int cacheMaximumNumberOfEntries, @NotNull final RecordStore<Identifier, AbstractMetadataRecord<?>> metadataRecordStore, @NotNull final ConcurrentAggregatedEventObserver<Identifier> concurrentAggregatedRepositoryEventObserver, @NotNull final IdentifierConstructor identifierConstructor)
	{
		return new ThreadLocal<Cache<Identifier, MetadataRecordSubResource>>()
		{
			@SuppressWarnings("RefusedBequest")
			@Override
			protected Cache<Identifier, MetadataRecordSubResource> initialValue()
			{
				final ThreadUnsafeLeastRecentlyAccessedCache<Identifier, MetadataRecordSubResource> cache = new ThreadUnsafeLeastRecentlyAccessedCache<>(cacheMaximumNumberOfEntries, new MetadataRecordSubResourceSourceOfValuesToCache(metadataRecordStore, identifierConstructor));
				concurrentAggregatedRepositoryEventObserver.add(new InvalidateCacheEventObserver<>(cache));
				return cache;
			}
		};
	}

	public MetadataRecordResourceStateSnapshot(@NotNull final IdentifierConstructor identifierConstructor, final int cacheMaximumNumberOfEntries, @NotNull final RecordStore<Identifier, AbstractMetadataRecord<?>> metadataRecordStore, @NotNull final ConcurrentAggregatedEventObserver<Identifier> concurrentAggregatedRepositoryEventObserver)
	{
		this.identifierConstructor = identifierConstructor;
		final String name = identifierConstructor.name();
		iterationDisallowed = format(UK, "Iteration of %1$s identifiers is not supported", name);
		unknownIdentifier = "Unknown " + name;
		cacheThreadLocal = identifierRecordCache(cacheMaximumNumberOfEntries, metadataRecordStore, concurrentAggregatedRepositoryEventObserver, identifierConstructor);
	}

	@NotNull
	@Override
	public SubResource find(@NotNull final String rawPath) throws NotFoundException
	{
		if (rawPath.isEmpty())
		{
			throw new NotFoundException(iterationDisallowed);
		}
		if (rawPath.contains("/"))
		{
			throw new NotFoundException("/ is not used");
		}

		final UUID uuid;
		try
		{
			uuid = UUID.fromString(rawPath);
		}
		catch(RuntimeException e)
		{
			throw new NotFoundException("Invalid UUID", e);
		}
		final Identifier identifier = identifierConstructor.construct(uuid);
		final Cache<Identifier, MetadataRecordSubResource> cache = cacheThreadLocal.get();
		@Nullable final SubResource subResource = cache.get(identifier);
		if (subResource == null)
		{
			throw new NotFoundException(unknownIdentifier);
		}
		return subResource;
	}

}
