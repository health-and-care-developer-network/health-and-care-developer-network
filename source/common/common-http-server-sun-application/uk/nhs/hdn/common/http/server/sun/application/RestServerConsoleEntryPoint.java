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

package uk.nhs.hdn.common.http.server.sun.application;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.server.sun.Server;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpointsFactories.CouldNotCreateRestEndpointsException;
import uk.nhs.hdn.common.http.server.sun.restEndpointsFactories.RestEndpointsFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static uk.nhs.hdn.common.commandLine.ExitHelper.exitWithException;

public final class RestServerConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String DataPathOption = "data-path";

	private static final String DefaultHostName = "localhost";
	private static final int DefaultHttpPort = 8080;
	private static final int DefaultBacklog = 20;
	private static final String DefaultDataPath = "/srv/hdn/http";

	@NotNull
	private final RestEndpointsFactory restEndpointsFactory;

	public RestServerConsoleEntryPoint(@NotNull final RestEndpointsFactory restEndpointsFactory)
	{
		this.restEndpointsFactory = restEndpointsFactory;
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DomainNameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultHostName).describedAs("domain name to list on");
		options.accepts(HttpPortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHttpPort).describedAs("port to listen for HTTP on");
		options.accepts(BacklogOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultBacklog).describedAs("TCP connection backlog");
		options.accepts(DataPathOption).withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Folder path containing data to server");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException
	{
		final String domainName = defaulted(optionSet, DomainNameOption);

		final char httpPort = portNumber(optionSet, HttpPortOption);

		final int backlog = unsignedInteger(optionSet, BacklogOption);

		final File dataPath = readableDirectory(optionSet, DataPathOption);

		final RestEndpoint[] restEndpoints;
		try
		{
			restEndpoints = restEndpointsFactory.restEndpoints(dataPath);
		}
		catch (CouldNotCreateRestEndpointsException e)
		{
			exitWithException(e);
			return;
		}
		final Server server = new Server(new InetSocketAddress(domainName, (int) httpPort), backlog, restEndpoints);
		server.start();
	}

}
