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

package uk.nhs.hdn.crds.store.recordStore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.store.server.eventObservers.ConcurrentAggregatedEventObserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Collections.emptyMap;

public final class SubstitutableRecordStore<K, V> extends AbstractToString implements RecordStore<K, V>
{
	@NotNull private final ConcurrentAggregatedEventObserver<K> concurrentAggregatedEventObserver;
	@NotNull private Map<K,V> data;
	private final Lock readLock;
	private final Lock writeLock;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public SubstitutableRecordStore(@NotNull final ConcurrentAggregatedEventObserver<K> concurrentAggregatedEventObserver)
	{
		this.concurrentAggregatedEventObserver = concurrentAggregatedEventObserver;
		data = emptyMap();
		readLock = new ReentrantReadWriteLock(false).readLock();
		writeLock = new ReentrantReadWriteLock(false).writeLock();
	}

	// Any Thread
	@Nullable
	@Override
	public V get(@NotNull final K identifier)
	{
		readLock.lock();
		try
		{
			return data.get(identifier);
		}
		finally
		{
			readLock.unlock();
		}
	}

	// Only 1 Writer Thread
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public void substituteNewBackingData(@NotNull final Map<K, V> data)
	{
		@SuppressWarnings("TooBroadScope") final Set<K> oldKeysForCacheInvalidation;
		writeLock.lock();
		try
		{
			oldKeysForCacheInvalidation = data.keySet();
			this.data = data;
		}
		finally
		{
			writeLock.unlock();
		}
		// cache invalidation does not need to happen whilst holding the writeLock. It is fine-grainded to check for what's changed, but slower and of dubious benefit
		for (final K key : oldKeysForCacheInvalidation)
		{
			concurrentAggregatedEventObserver.storeChanged(key);
		}
	}
}
