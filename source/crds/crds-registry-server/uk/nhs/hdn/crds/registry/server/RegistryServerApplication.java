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

package uk.nhs.hdn.crds.registry.server;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hdn.common.fileWatching.FileReloader;
import uk.nhs.hdn.common.http.server.sun.Server;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.domain.identifiers.*;
import uk.nhs.hdn.crds.registry.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.registry.recordStore.SubstitutableRecordStore;
import uk.nhs.hdn.crds.registry.server.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.registry.server.rest.PatientRecordStoreRestEndpoint;
import uk.nhs.hdn.crds.registry.server.rest.metadata.MetadataRecordRestEndpoint;
import uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread.StartReceivingMessagesThread;
import uk.nhs.hdn.number.NhsNumber;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Runtime.getRuntime;
import static java.util.UUID.fromString;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.fileWatching.FileWatcher.startFileWatcherOnNewThread;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.RootDenialRestEndpoint.RootDenialRestEndpointInstance;
import static uk.nhs.hdn.common.parsers.ParsingFileReloader.utf8ParsingFileReloaderWithInitialLoad;
import static uk.nhs.hdn.crds.registry.domain.StuffEventKind.Created;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.*;
import static uk.nhs.hdn.crds.registry.domain.metadata.parsing.MetadataRecordsParserFactory.metadataRecordsParser;
import static uk.nhs.hdn.number.NhsNumber.valueOf;

public final class RegistryServerApplication
{
	@NonNls private static final String RegistryMetadataFileName = "crds-registry-server-metadata.tsv";

	public static void run(@NonNls @NotNull final String domainName, final char httpPort, final int backlog, final int cacheMaximumNumberOfEntries, @NotNull final PatientRecordStoreKind patientRecordStoreKind, @NotNull final File dataPath, @NotNull final StartReceivingMessagesThread startReceivingMessagesThread, final HazelcastConfiguration hazelcastConfiguration) throws IOException
	{
		final ConcurrentAggregatedEventObserver<NhsNumber> patientRecordConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final PatientRecordStore patientRecordStore = patientRecordStoreKind.create(hazelcastConfiguration, patientRecordConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> providerMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore = new SubstitutableRecordStore<>(providerMetadataConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> repositoryMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore = new SubstitutableRecordStore<>(repositoryMetadataConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> stuffMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> stuffMetadataRecordStore = new SubstitutableRecordStore<>(repositoryMetadataConcurrentAggregatedEventObserver);

		seedPatientRecordStoreWithExampleData(patientRecordStore);

		final AtomicBoolean terminationSignal = startReceivingMessagesThread.startReceivingMessagesThread(patientRecordStore);

		final FileReloader fileReloader;
		try
		{
			fileReloader = utf8ParsingFileReloaderWithInitialLoad(metadataRecordsParser(providerMetadataRecordStore, repositoryMetadataRecordStore, stuffMetadataRecordStore), dataPath, RegistryMetadataFileName);
		}
		catch (FailedToReloadException e)
		{
			throw new IllegalStateException("Could not load registry metadata", e);
		}
		startFileWatcherOnNewThread(dataPath, RegistryMetadataFileName, fileReloader);

		final Server server = new Server(new InetSocketAddress(domainName, (int) httpPort), backlog, of
		(
			RootDenialRestEndpointInstance,
			new PatientRecordStoreRestEndpoint(cacheMaximumNumberOfEntries, patientRecordStore, patientRecordConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(provider, cacheMaximumNumberOfEntries, providerMetadataRecordStore, providerMetadataConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(repository, cacheMaximumNumberOfEntries, repositoryMetadataRecordStore, repositoryMetadataConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(stuff, cacheMaximumNumberOfEntries, stuffMetadataRecordStore, stuffMetadataConcurrentAggregatedEventObserver)
		));
		server.start();

		getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				server.stop();
				terminationSignal.set(true);
			}
		}));
	}

	@SuppressWarnings("MagicNumber")
	private static void seedPatientRecordStoreWithExampleData(final PatientRecordStore patientRecordStore)
	{
		patientRecordStore.addEvent(new StuffEventMessage
		(
			valueOf("1234567880"),
			new ProviderIdentifier(fromString("2dbf298f-eed9-474d-bf8b-d70f68b83417")),
			new RepositoryIdentifier(fromString("66dad8b0-72c7-4164-a8b2-27ae6b7467cf")),
			new StuffIdentifier(fromString("599dbd25-3c3e-4b7a-868b-37b653f394dd")),
			new StuffEvent
			(
				new StuffEventIdentifier(fromString("4e3ccfac-fa2e-4562-8320-b11fd7accd03")),
				1366365019L,
				Created
			)
		));
	}

	private RegistryServerApplication()
	{
	}
}
