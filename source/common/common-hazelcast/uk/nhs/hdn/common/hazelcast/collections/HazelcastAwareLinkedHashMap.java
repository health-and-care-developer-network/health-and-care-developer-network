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

package uk.nhs.hdn.common.hazelcast.collections;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hdn.common.serialisers.GenericMapSerialisable.serialiseMapGenerically;

@SuppressWarnings({"SerializableHasSerializationMethods", "serial"})
public final class HazelcastAwareLinkedHashMap<K extends HazelcastDataWriter, V extends HazelcastDataWriter> extends HashMap<K, V> implements HazelcastDataWriter, MapSerialisable
{
	public static final float OptimumHashLoadFactor = 0.7f;

	public HazelcastAwareLinkedHashMap(final int size)
	{
		super(size, OptimumHashLoadFactor);
	}

	public HazelcastAwareLinkedHashMap(@NotNull final K key0, @NotNull final V value0)
	{
		super(1, OptimumHashLoadFactor);
		put(key0, value0);
	}

	public HazelcastAwareLinkedHashMap(@NotNull final Map<K, V> map, @NotNull final K key0, @NotNull final V value0)
	{
		super(map.size(), OptimumHashLoadFactor);
		putAll(map);
		put(key0, value0);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		out.writeInt(size());
		for (final Map.Entry<K, V> entry : entrySet())
		{
			entry.getKey().writeData(out);
			entry.getValue().writeData(out);
		}
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		serialiseMapGenerically(mapSerialiser, this);
	}

	@NotNull
	public static <K extends HazelcastDataWriter, V extends HazelcastDataWriter> HazelcastAwareLinkedHashMap<K, V> readData(@NotNull final DataInput in, @NotNull final HazelcastDataReader<K> keyReader, @NotNull final HazelcastDataReader<V> valueReader) throws IOException
	{
		final int size = in.readInt();
		final HazelcastAwareLinkedHashMap<K, V> map = new HazelcastAwareLinkedHashMap<>(size);
		for(int index = 0; index < size; index++)
		{
			final K key = keyReader.readData(in);
			final V value = valueReader.readData(in);
			map.put(key, value);
		}
		return map;
	}
}
