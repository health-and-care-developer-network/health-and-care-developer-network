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

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.caching.caching.Cache;

public final class InvalidateCacheEventObserver<K> extends AbstractEventObserver<K>
{
	private final Cache<K, ?> cache;

	public InvalidateCacheEventObserver(@NotNull final Cache<K, ?> cache)
	{
		this.cache = cache;
	}

	@Override
	public void storeChanged(@NotNull final K key)
	{
		cache.invalidate(key);
	}
}