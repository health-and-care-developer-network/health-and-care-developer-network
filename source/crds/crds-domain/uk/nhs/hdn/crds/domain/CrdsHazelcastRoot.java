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

package uk.nhs.hdn.crds.domain;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.number.NhsNumber;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static uk.nhs.hdn.crds.domain.RepositoryEventKind.Created;

public final class CrdsHazelcastRoot
{
	@NotNull private static final String RootMap = "root-map3";

	@NotNull public static final CrdsHazelcastRoot CrdsHazelcastRootInstance = new CrdsHazelcastRoot();

	@NotNull private final HazelcastInstance hazelcastInstance;

	private CrdsHazelcastRoot()
	{
		final Config config = new Config();
		final MapConfig rootMapConfig = new MapConfig(RootMap);
		rootMapConfig.addMapIndexConfig(new MapIndexConfig());

//		rootMapConfig.setBackupCounts(1, 1);
//		rootMapConfig.setCacheValue(false);
//		rootMapConfig.setClearQuick(true);
//		rootMapConfig.setEvictionPercentage(0);
//		rootMapConfig.setEvictionPolicy("NONE");

//		final MapStoreConfig mapStoreConfig = new MapStoreConfig();
//		mapStoreConfig.setClassName("com.hazelcast.examples.DummyStore");
//		mapStoreConfig.setEnabled(true);
//		//mapStoreConfig.setFactoryClassName();
//		//mapStoreConfig.setFactoryImplementation();
//		//mapStoreConfig.setImplementation();
//		//mapStoreConfig.setProperties();
//		mapStoreConfig.setWriteDelaySeconds(0);
//		rootMapConfig.setMapStoreConfig(mapStoreConfig);

//		rootMapConfig.setMaxIdleSeconds(0);
//		final MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
//		maxSizeConfig.setMaxSizePolicy("cluster_wide_map_size");
//		maxSizeConfig.setSize(0);
//		rootMapConfig.setMaxSizeConfig(maxSizeConfig);
		//rootMapConfig.setMergePolicy();

//		final NearCacheConfig nearCacheConfig = new NearCacheConfig(0, 0, "NONE", 0, true);
//		rootMapConfig.setNearCacheConfig(nearCacheConfig);
//		rootMapConfig.setReadBackupData(false);
//		rootMapConfig.setStorageType(HEAP);
//		rootMapConfig.setTimeToLiveSeconds(0);
//		rootMapConfig.setValueIndexed(false);
//		final WanReplicationRef wanReplicationRef = new WanReplicationRef();
//		rootMapConfig.setWanReplicationRef(wanReplicationRef);
		config.addMapConfig(rootMapConfig);
		hazelcastInstance = newHazelcastInstance(config);
	}

	@NotNull
	public RootMap rootMap()
	{
		return new RootMap(hazelcastInstance.<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder>getMap(RootMap));
	}

	public static void main(@NotNull final String... args)
	{
		final RootMap rootMap = CrdsHazelcastRootInstance.rootMap();

		rootMap.addEvent(NhsNumber.valueOf("1234567881"), new ProviderIdentifier(randomUUID()), new RepositoryIdentifier(randomUUID()), new RepositoryEvent(new RepositoryEventIdentifier(randomUUID()), currentTimeMillis(), Created));
	}
}
