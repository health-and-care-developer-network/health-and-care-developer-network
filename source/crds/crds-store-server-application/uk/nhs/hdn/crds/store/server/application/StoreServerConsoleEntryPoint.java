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

package uk.nhs.hdn.crds.store.server.application;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hdn.common.fileWatching.FileReloader;
import uk.nhs.hdn.common.http.server.sun.Server;
import uk.nhs.hdn.crds.store.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.parsing.ProviderAndRepositoryMetadataParserFactory;
import uk.nhs.hdn.crds.store.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.store.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.store.recordStore.SubstitutableRecordStore;
import uk.nhs.hdn.crds.store.rest.PatientRecordStoreRestEndpoint;
import uk.nhs.hdn.crds.store.rest.metadata.MetadataRecordRestEndpoint;
import uk.nhs.hdn.number.NhsNumber;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.fileWatching.FileWatcher.startFileWatcherOnNewThread;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.RootDenialRestEndpoint.RootDenialRestEndpointInstance;
import static uk.nhs.hdn.common.parsers.ParsingFileReloader.utf8ParsingFileReloaderWithInitialLoad;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Repository;
import static uk.nhs.hdn.crds.store.server.application.PatientRecordStoreKind.Hazelcast;

public final class StoreServerConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String CacheSizeOption = "cache-size";
	private static final String PatientRecordStoreKindOption = "patient-record-store-kind";
	private static final String HazelcasePortOption = "hazelcast-port";
	private static final String DataPathOption = "data-path";

	private static final String DefaultHostName = "localhost";
	private static final int DefaultHttpPort = 7000;
	private static final int DefaultBacklog = 20;
	private static final int DefaultCacheSize = 10000;
	private static final PatientRecordStoreKind DefaultPatientRecordStoreKind = Hazelcast;
	private static final int DefaultHazlecastPort = 10000;
	private static final String DefaultDataPath = "/srv/hdn-crds-store";

	@NonNls private static final String StoreMetadataFileName = "crds-store-server-metadata.tsv";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(StoreServerConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DomainNameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultHostName).describedAs("domain name to list on");
		options.accepts(HttpPortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHttpPort).describedAs("port to listen for HTTP on");
		options.accepts(BacklogOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultBacklog).describedAs("TCP connection backlog");
		options.accepts(CacheSizeOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultCacheSize).describedAs("maximum number of entries to cache per I/O thread");
		options.accepts(PatientRecordStoreKindOption).withRequiredArg().ofType(PatientRecordStoreKind.class).defaultsTo(DefaultPatientRecordStoreKind).describedAs("backing store kind for data");
		options.accepts(HazelcasePortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHazlecastPort).describedAs("first port for Hazelcast to listen on");
		options.accepts(DataPathOption).withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Folder path containing store metadata");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException
	{
		final String domainName = defaulted(optionSet, DomainNameOption);

		final char httpPort = portNumber(optionSet, HttpPortOption);

		final int backlog = unsignedInteger(optionSet, BacklogOption);

		final int cacheMaximumNumberOfEntries = unsignedInteger(optionSet, CacheSizeOption);

		final PatientRecordStoreKind patientRecordStoreKind = defaulted(optionSet, PatientRecordStoreKindOption);

		final char hazelcastPort = portNumber(optionSet, HazelcasePortOption);

		final File dataPath = readableDirectory(optionSet, DataPathOption);

		final ConcurrentAggregatedEventObserver<NhsNumber> patientRecordConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final PatientRecordStore patientRecordStore = patientRecordStoreKind.create(new HazelcastConfiguration(hazelcastPort), patientRecordConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> providerMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore = new SubstitutableRecordStore<>(providerMetadataConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> repositoryMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore = new SubstitutableRecordStore<>(repositoryMetadataConcurrentAggregatedEventObserver);

		final FileReloader fileReloader;
		try
		{
			fileReloader = utf8ParsingFileReloaderWithInitialLoad(new ProviderAndRepositoryMetadataParserFactory(providerMetadataRecordStore, repositoryMetadataRecordStore), dataPath, StoreMetadataFileName);
		}
		catch (FailedToReloadException e)
		{
			throw new IllegalStateException("Could not load store metadata", e);
		}
		startFileWatcherOnNewThread(dataPath, StoreMetadataFileName, fileReloader);

		final Server server = new Server(new InetSocketAddress(domainName, (int) httpPort), backlog, of
		(
			RootDenialRestEndpointInstance,
			new PatientRecordStoreRestEndpoint(cacheMaximumNumberOfEntries, patientRecordStore, patientRecordConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(Provider, cacheMaximumNumberOfEntries, providerMetadataRecordStore, providerMetadataConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(Repository, cacheMaximumNumberOfEntries, repositoryMetadataRecordStore, repositoryMetadataConcurrentAggregatedEventObserver)
		));
		server.start();
	}
}
