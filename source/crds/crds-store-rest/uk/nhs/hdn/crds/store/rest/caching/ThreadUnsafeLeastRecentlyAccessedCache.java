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

package uk.nhs.hdn.crds.store.rest.caching;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.store.rest.RepositoryEventObserver;
import uk.nhs.hdn.crds.store.rest.SourceOfValuesToCache;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ThreadUnsafeLeastRecentlyAccessedCache<K, V> implements RepositoryEventObserver<K>
{
	@NotNull private final ConcurrentLinkedQueue<K> events;
	@NotNull private final Map<K, SoftReference<V>> cache;
	@NotNull private final SourceOfValuesToCache<K, V> sourceOfValuesToCache;

	public ThreadUnsafeLeastRecentlyAccessedCache(final int maximumSize, @NotNull final SourceOfValuesToCache<K, V> sourceOfValuesToCache)
	{
		this.sourceOfValuesToCache = sourceOfValuesToCache;
		events = new ConcurrentLinkedQueue<>();
		cache = new ThreadUnsafeLeastRecentlyAccessedLinkedHashMap<>(maximumSize);
	}

	// Thread 1
	@Override
	public void repositoryEventReceived(@NotNull final K key)
	{
		events.add(key);
	}

	// Thread 2
	@Nullable
	public V get(@NotNull final K key)
	{
		processAnyEvents();

		@Nullable final SoftReference<V> valueReference = cache.get(key);
		if (valueReference == null)
		{
			@Nullable final V subResource = sourceOfValuesToCache.get(key);
			if (subResource != null)
			{
				cache.put(key, new SoftReference<>(subResource));
			}
			return subResource;
		}
		else
		{
			@Nullable V subResource = valueReference.get();
			if (subResource == null)
			{
				cache.remove(key);
				subResource = sourceOfValuesToCache.get(key);
				if (subResource != null)
				{
					cache.put(key, new SoftReference<>(subResource));
				}
			}
			return subResource;
		}
	}

	private void processAnyEvents()
	{
		do
		{
			@Nullable final K event = events.poll();
			if (event == null)
			{
				return;
			}
			cache.remove(event);

		} while (true);
	}

}
