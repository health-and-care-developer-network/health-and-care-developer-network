/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.application;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.commandLine.ExitHelper;
import uk.nhs.hcdn.common.http.server.sun.Server;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.CouldNotCreateRestEndpointsException;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.RestEndpointsFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static uk.nhs.hcdn.common.commandLine.ExitHelper.exitWithException;

public final class ServerApplication
{
	private static final String HelpOption = "help";
	private static final String HostnameOption = "hostname";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String DataPathOption = "data-path";

	private static final String DefaultHostName = "localhost";
	private static final int DefaultHttpPort = 8080;
	private static final int DefaultBacklog = 20;
	private static final String DefaultDataPath = "/srv/hcdn/http";

	@SuppressWarnings("unchecked")
	@NotNull
	private static final OptionParser Options = new OptionParser()
	{{
		accepts(HelpOption).forHelp();
		accepts(HostnameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultHostName).describedAs("hostname to list on");
		accepts(HttpPortOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHttpPort).describedAs("port to listen for HTTP on");
		accepts(BacklogOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultBacklog).describedAs("TCP connection backlog");
		accepts(DataPathOption).withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Folder path containing data to server");
	}};

	private static final ExitHelper ExitHelper = new ExitHelper(Options);
	private static final int MaximumPortNumber = 65535;

	private ServerApplication()
	{
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void start(@NotNull final String[] commandLineArguments, @NotNull final RestEndpointsFactory restEndpointsFactory)
	{
		try
		{
			final OptionSet optionSet;
			try
			{
				optionSet = Options.parse(commandLineArguments);
			}
			catch (OptionException e)
			{
				ExitHelper.exitWithErrorAndHelp(e.getMessage());
				return;
			}

			if (optionSet.has(HelpOption))
			{
				ExitHelper.exitWithHelp();
				return;
			}

			if (!optionSet.nonOptionArguments().isEmpty())
			{
				ExitHelper.exitWithErrorAndHelp("Non-option arguments are not understood");
				return;
			}

			@NotNull final String hostName = (String) optionSet.valueOf(HostnameOption);

			final int httpPort = (Integer) optionSet.valueOf(HttpPortOption);
			if (httpPort < 1 || httpPort > MaximumPortNumber)
			{
				ExitHelper.exitWithErrorAndHelp("http-port %1$s must be between 1 and 65535 inclusive", httpPort);
			}

			final int backlog = (Integer) optionSet.valueOf(BacklogOption);
			if (backlog < 0)
			{
				ExitHelper.exitWithErrorAndHelp("backlog %1$s must be positive", backlog);
			}

			final File dataPath = (File) optionSet.valueOf(DataPathOption);
			if (!(dataPath.exists() && dataPath.canRead() && dataPath.isDirectory()))
			{
				ExitHelper.exitWithErrorAndHelp("data-path %1$s does not exist as a readable directory", dataPath);
				return;
			}

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
			final Server server = new Server(new InetSocketAddress(hostName, httpPort), backlog, restEndpoints);
			server.start();
		}
		catch (IOException e)
		{
			exitWithException(e);
		}
	}

}
