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

package uk.nhs.hdn.crds.store.server.eventObservers;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public abstract class AbstractAdaptToEventObserverEntryListener<K, V, W> extends AbstractToString implements EntryListener<K, V>
{
	private final EventObserver<W> eventObserver;

	protected AbstractAdaptToEventObserverEntryListener(@NotNull final EventObserver<W> eventObserver)
	{
		this.eventObserver = eventObserver;
	}

	@Override
	public final void entryAdded(final EntryEvent<K, V> event)
	{
		raiseEvent(event);
	}

	@Override
	public final void entryRemoved(final EntryEvent<K, V> event)
	{
		raiseEvent(event);
	}

	@Override
	public final void entryUpdated(final EntryEvent<K, V> event)
	{
		raiseEvent(event);
	}

	@Override
	public final void entryEvicted(final EntryEvent<K, V> event)
	{
		raiseEvent(event);
	}

	private void raiseEvent(final EntryEvent<K, V> event)
	{
		final K key = event.getKey();
		eventObserver.storeChanged(toActualKey(key));
	}

	@NotNull
	protected abstract W toActualKey(@NotNull final K key);
}
