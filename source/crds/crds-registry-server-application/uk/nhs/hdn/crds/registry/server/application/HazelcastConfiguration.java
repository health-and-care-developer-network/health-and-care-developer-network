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

package uk.nhs.hdn.crds.registry.server.application;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Address;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.server.eventObservers.EventObserver;
import uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders.NhsNumberHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders.PatientRecordHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.registry.server.hazelcast.patientRecordStore.HazelcastPatientRecordStore;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.number.NhsNumber;

import java.net.UnknownHostException;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

public final class HazelcastConfiguration
{
	@NotNull private static final String PatientRecordStoreMap = "patient-record-registry";

	@NotNull private final HazelcastInstance hazelcastInstance;

	public HazelcastConfiguration(final char hazelcastPort, final boolean useTcp)
	{
		final Config config = new Config();

		final NetworkConfig networkConfig = new NetworkConfig();
		networkConfig.setPort((int) hazelcastPort);
		networkConfig.setPortAutoIncrement(true);
		networkConfig.setReuseAddress(true);
		if (useTcp)
		{

			final Address address;
			try
			{
				address = new Address("127.0.0.1", (int) hazelcastPort);
			}
			catch (UnknownHostException e)
			{
				throw new IllegalStateException(e);
			}
			final Join join = new Join();
			join.getMulticastConfig().setEnabled(false);
			networkConfig.setJoin(join.setTcpIpConfig(new TcpIpConfig().setEnabled(true).setConnectionTimeoutSeconds(60)));
		}

		config.setNetworkConfig(networkConfig);

		final MapConfig rootMapConfig = new MapConfig(PatientRecordStoreMap);
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
	public PatientRecordStore rootMap(@NotNull final EventObserver<NhsNumber> eventObserver)
	{
		return new HazelcastPatientRecordStore(hazelcastInstance.<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder>getMap(PatientRecordStoreMap), eventObserver);
	}
}
