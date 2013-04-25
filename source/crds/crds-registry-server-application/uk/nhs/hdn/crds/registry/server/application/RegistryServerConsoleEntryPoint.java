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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hdn.common.fileWatching.FileReloader;
import uk.nhs.hdn.common.http.server.sun.Server;
import uk.nhs.hdn.common.tuples.Quintuple;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.registry.domain.metadata.parsing.MetadataRecordsParserFactory;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.registry.recordStore.SubstitutableRecordStore;
import uk.nhs.hdn.crds.registry.server.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.registry.server.rest.PatientRecordStoreRestEndpoint;
import uk.nhs.hdn.crds.registry.server.rest.metadata.MetadataRecordRestEndpoint;
import uk.nhs.hdn.number.NhsNumber;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;

import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.fileWatching.FileWatcher.startFileWatcherOnNewThread;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.RootDenialRestEndpoint.RootDenialRestEndpointInstance;
import static uk.nhs.hdn.common.parsers.ParsingFileReloader.utf8ParsingFileReloaderWithInitialLoad;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Repository;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.Stuff;
import static uk.nhs.hdn.crds.registry.server.application.PatientRecordStoreKind.Hazelcast;

public final class RegistryServerConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String CacheSizeOption = "cache-size";
	private static final String PatientRecordStoreKindOption = "patient-record-registry-kind";
	private static final String HazelcasePortOption = "hazelcast-port";
	private static final String HazelcaseTcpOption = "hazelcast-tcp";
	private static final String DataPathOption = "data-path";

	private static final String DefaultHostName = "localhost";
	private static final int DefaultHttpPort = 4000;
	private static final int DefaultBacklog = 20;
	private static final int DefaultCacheSize = 10000;
	private static final PatientRecordStoreKind DefaultPatientRecordStoreKind = Hazelcast;
	private static final int DefaultHazlecastPort = 5701;
	private static final boolean DefaultHazlecastTcp = false;
	private static final String DefaultDataPath = "/srv/hdn-crds-registry-server";

	@NonNls private static final String RegistryMetadataFileName = "crds-registry-server-metadata.tsv";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(RegistryServerConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DomainNameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultHostName).describedAs("domain name to list on");
		options.accepts(HttpPortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHttpPort).describedAs("port to listen for HTTP on");
		options.accepts(BacklogOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultBacklog).describedAs("TCP connection backlog");
		options.accepts(CacheSizeOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultCacheSize).describedAs("maximum number of entries to cache per I/O thread");
		options.accepts(PatientRecordStoreKindOption).withRequiredArg().ofType(PatientRecordStoreKind.class).defaultsTo(DefaultPatientRecordStoreKind).describedAs("backing registry kind for data");
		options.accepts(HazelcasePortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHazlecastPort).describedAs("first port for Hazelcast to listen on");
		options.accepts(HazelcaseTcpOption, "Use multicast (false) or tcp (true)").withOptionalArg().ofType(Boolean.class).defaultsTo(DefaultHazlecastTcp).describedAs("defaults to using TCP instead of multicast");
		options.accepts(DataPathOption).withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Folder path containing registry metadata");
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

		final boolean useTcp;
		if (optionSet.has(HazelcaseTcpOption))
		{
			if (optionSet.hasArgument(HazelcaseTcpOption))
			{
				useTcp = defaulted(optionSet, HazelcaseTcpOption);
			}
			else
			{
				useTcp = true;
			}
		}
		else
		{
			useTcp = DefaultHazlecastTcp;
		}

		final File dataPath = readableDirectory(optionSet, DataPathOption);

		execute(domainName, httpPort, backlog, cacheMaximumNumberOfEntries, patientRecordStoreKind, hazelcastPort, useTcp, dataPath);
	}

	private static void execute(final String domainName, final char httpPort, final int backlog, final int cacheMaximumNumberOfEntries, final PatientRecordStoreKind patientRecordStoreKind, final char hazelcastPort, final boolean useTcp, final File dataPath) throws IOException
	{
		final ConcurrentAggregatedEventObserver<NhsNumber> patientRecordConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final PatientRecordStore patientRecordStore = patientRecordStoreKind.create(new HazelcastConfiguration(hazelcastPort, useTcp), patientRecordConcurrentAggregatedEventObserver);

		new Thread(new EventListenerRunnable(new LinkedBlockingDeque<Quintuple<NhsNumber, ProviderIdentifier, RepositoryIdentifier, StuffIdentifier, StuffEvent>>(), patientRecordStore), "Incoming Events Listener").start();

		final ConcurrentAggregatedEventObserver<Identifier> providerMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> providerMetadataRecordStore = new SubstitutableRecordStore<>(providerMetadataConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> repositoryMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> repositoryMetadataRecordStore = new SubstitutableRecordStore<>(repositoryMetadataConcurrentAggregatedEventObserver);

		final ConcurrentAggregatedEventObserver<Identifier> stuffMetadataConcurrentAggregatedEventObserver = new ConcurrentAggregatedEventObserver<>();
		final SubstitutableRecordStore<Identifier, AbstractMetadataRecord<?>> stuffMetadataRecordStore = new SubstitutableRecordStore<>(repositoryMetadataConcurrentAggregatedEventObserver);

		final FileReloader fileReloader;
		try
		{
			fileReloader = utf8ParsingFileReloaderWithInitialLoad(new MetadataRecordsParserFactory(providerMetadataRecordStore, repositoryMetadataRecordStore, stuffMetadataRecordStore), dataPath, RegistryMetadataFileName);
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
			new MetadataRecordRestEndpoint(Provider, cacheMaximumNumberOfEntries, providerMetadataRecordStore, providerMetadataConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(Repository, cacheMaximumNumberOfEntries, repositoryMetadataRecordStore, repositoryMetadataConcurrentAggregatedEventObserver),
			new MetadataRecordRestEndpoint(Stuff, cacheMaximumNumberOfEntries, stuffMetadataRecordStore, stuffMetadataConcurrentAggregatedEventObserver)
		));
		server.start();
	}

}
