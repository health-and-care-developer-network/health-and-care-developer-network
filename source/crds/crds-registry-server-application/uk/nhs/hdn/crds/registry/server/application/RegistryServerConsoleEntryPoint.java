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
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.registry.server.PatientRecordStoreKind;
import uk.nhs.hdn.crds.registry.server.application.hazelcast.HazelcastStartReceivingMessagesThread;

import java.io.File;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.server.HazelcastConfiguration.DefaultHazelcastPort;
import static uk.nhs.hdn.crds.registry.server.PatientRecordStoreKind.Hazelcast;
import static uk.nhs.hdn.crds.registry.server.RegistryServerApplication.run;

public final class RegistryServerConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String CacheSizeOption = "cache-size";
	private static final String PatientRecordStoreKindOption = "patient-record-registry-kind";
	private static final String HazelcasePortOption = "hazelcast-port";
	private static final String DataPathOption = "data-path";
	private static final String InstanceIdOption = "instance-id";
	private static final String PgpassFileOption = "pgpass-file";

	private static final String DefaultHostName = "localhost";
	private static final int DefaultHttpPort = 4000;
	private static final int DefaultBacklog = 20;
	private static final int DefaultCacheSize = 10000;
	private static final PatientRecordStoreKind DefaultPatientRecordStoreKind = Hazelcast;
	private static final String DefaultDataPath = "/srv/hdn-crds-registry-server";
	private static final int DefaultInstanceId = 0;

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
		options.accepts(HazelcasePortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHazelcastPort).describedAs("first port for Hazelcast to listen on");
		options.accepts(DataPathOption, "Folder path containing registry metadata and local container data").withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Linux path");
		options.accepts(InstanceIdOption, "long lived instance identifier").withRequiredArg().ofType(Integer.class).defaultsTo(DefaultInstanceId).describedAs("Instance identifer. Must be unique but consistent across invocations");
		options.accepts(PgpassFileOption, "location of password file if not ~/.pgpass").withRequiredArg().ofType(File.class);
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

		final int instanceId = defaulted(optionSet, InstanceIdOption);

		final File pgpassFile;
		if (optionSet.has(PgpassFileOption))
		{
			pgpassFile = readableFile(optionSet, PgpassFileOption);
		}
		else
		{
			pgpassFile = new File(".pgpass"); //findDefaultPgpassFileIfNoneSpecified(null);
		}

		final HazelcastConfiguration hazelcastConfiguration = new HazelcastConfiguration(hazelcastPort);
		run(domainName, httpPort, backlog, cacheMaximumNumberOfEntries, patientRecordStoreKind, dataPath, new HazelcastStartReceivingMessagesThread(hazelcastConfiguration), hazelcastConfiguration);
	}

}
