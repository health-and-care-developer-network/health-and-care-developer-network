/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.application;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.Server;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.CouldNotCreateRestEndpointsException;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.RestEndpointsFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static java.lang.System.err;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.commandLine.ExitCode.GeneralError;
import static uk.nhs.hcdn.common.commandLine.ExitCode.Help;

public final class ServerApplication
{
	private static final String HelpOption = "help";
	private static final String HttpPortOption = "http-port";
	private static final String BacklogOption = "backlog";
	private static final String DataPathOption = "data-path";

	private static final char DefaultHttpPort = (char) 8080;
	private static final int DefaultBacklog = 20;
	private static final String DefaultDataPath = "/srv/hcdn/http";

	@SuppressWarnings("unchecked")
	@NotNull
	private static final OptionParser Options = new OptionParser()
	{{
		accepts(HelpOption).forHelp();
		accepts(HttpPortOption).withRequiredArg().ofType(Character.class).defaultsTo(DefaultHttpPort).describedAs("port to listen for HTTP on");
		accepts(BacklogOption).withRequiredArg().ofType(Integer.class).defaultsTo(DefaultBacklog).describedAs("TCP connection backlog");
		accepts(DataPathOption).withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultDataPath)).describedAs("Folder path containing data to server");
	}};

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
				err.println(e.getMessage());
				Options.printHelpOn(err);
				GeneralError.exit();
				return;
			}

			if (optionSet.has(HelpOption))
			{
				Options.printHelpOn(err);
				Help.exit();
				return;
			}

			if (!optionSet.nonOptionArguments().isEmpty())
			{
				err.println("Non-option arguments are not understood");
				Options.printHelpOn(err);
				GeneralError.exit();
			}

			final char httpPort = (Character) optionSet.valueOf(HttpPortOption);
			final int backlog = (Integer) optionSet.valueOf(BacklogOption);
			final File dataPath = (File) optionSet.valueOf(DataPathOption);
			if (!(dataPath.exists() && dataPath.canRead() && dataPath.isDirectory()))
			{
				err.format(ENGLISH, "data-path %1$s does not exist as a readable directory", dataPath.toPath().toString());
				err.println();
				Options.printHelpOn(err);
				GeneralError.exit();
				return;
			}

			final RestEndpoint[] restEndpoints;
			try
			{
				restEndpoints = restEndpointsFactory.restEndpoints(dataPath);
			}
			catch (CouldNotCreateRestEndpointsException e)
			{
				e.printStackTrace();
				GeneralError.exit();
				return;
			}
			final Server server = new Server(new InetSocketAddress((int) httpPort), backlog, restEndpoints);
			server.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			GeneralError.exit();
		}
	}
}
