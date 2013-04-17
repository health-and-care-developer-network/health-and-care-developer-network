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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.AbstractRegisterableMethodRoutingRestEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.GetMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.HeadMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hdn.crds.store.PatientRecordStore;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.store.rest.caching.ThreadUnsafeLeastRecentlyAccessedCache;
import uk.nhs.hdn.number.NhsNumber;

import java.util.Map;

public final class CrdsStoreRestEndpoint extends AbstractRegisterableMethodRoutingRestEndpoint<CrdsStoreResourceStateSnapshot>
{
	// TODO: Should be a lightweight client to the Hazelcast cluster
	public CrdsStoreRestEndpoint(@NonNls @NotNull final String absoluteRawPath)
	{
		super("/crds/store/", NoAuthentication);
	}

	@NotNull
	public static ThreadLocal<ThreadUnsafeLeastRecentlyAccessedCache<NhsNumber, PatientRecordSubResource>> patientRecordCache(final int maximumSize, @NotNull final PatientRecordStore patienntRecordStore)
	{
		return new ThreadLocal<ThreadUnsafeLeastRecentlyAccessedCache<NhsNumber, PatientRecordSubResource>>()
		{
			@SuppressWarnings("RefusedBequest")
			@Override
			protected ThreadUnsafeLeastRecentlyAccessedCache<NhsNumber, PatientRecordSubResource> initialValue()
			{
				return new ThreadUnsafeLeastRecentlyAccessedCache<>(maximumSize, new SourceOfValuesToCache<NhsNumber, PatientRecordSubResource>()
				{
					@Nullable
					@Override
					public PatientRecordSubResource get(@NotNull final NhsNumber key)
					{
						@Nullable final SimplePatientRecord simplePatientRecord = patienntRecordStore.get(key);
						if (simplePatientRecord == null)
						{
							return null;
						}
						return new PatientRecordSubResource(simplePatientRecord.lastModified(), 1000, "patientRecord", simplePatientRecord);
					}
				});
			}
		};
	}

	@Override
	protected void registerMethodEndpointsExcludingOptions(@NotNull final Map<String, MethodEndpoint<CrdsStoreResourceStateSnapshot>> methodEndpointsRegister)
	{
		HeadMethodEndpoint.<CrdsStoreResourceStateSnapshot>headMethodEndpoint().register(methodEndpointsRegister);
		GetMethodEndpoint.<CrdsStoreResourceStateSnapshot>getMethodEndpoint().register(methodEndpointsRegister);
	}

	@NotNull
	@Override
	protected CrdsStoreResourceStateSnapshot snapshotOfStateThatIsInvariantForRequest()
	{
		return new CrdsStoreResourceStateSnapshot();
	}
}
