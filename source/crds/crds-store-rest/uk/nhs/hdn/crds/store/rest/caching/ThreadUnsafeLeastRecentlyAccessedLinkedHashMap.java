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

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"SerializableHasSerializationMethods", "serial"})
public final class ThreadUnsafeLeastRecentlyAccessedLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
	private static final float OptimumLoadFactor = 0.7f;

	private final int sizeCheck;

	public ThreadUnsafeLeastRecentlyAccessedLinkedHashMap(final int maximumSize)
	{
		super(maximumSize + 1, OptimumLoadFactor, true);
		sizeCheck = maximumSize + 1;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest)
	{
		return size() == sizeCheck;
	}
}
