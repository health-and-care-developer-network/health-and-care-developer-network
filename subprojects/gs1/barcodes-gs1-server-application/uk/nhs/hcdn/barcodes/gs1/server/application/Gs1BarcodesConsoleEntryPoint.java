/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.application;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.server.Gs1CompanyPrefixRestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.RestEndpointsFactory;

import java.io.File;

import static uk.nhs.hcdn.common.http.server.sun.application.ServerApplication.start;

public final class Gs1BarcodesConsoleEntryPoint
{
	public static void main(@NotNull final String... commandLineArguments)
	{
		start(commandLineArguments, new RestEndpointsFactory()
		{
			@NotNull
			@Override
			public RestEndpoint[] restEndpoints(@NotNull final File dataPath)
			{
				return new RestEndpoint[]
				{
					new Gs1CompanyPrefixRestEndpoint(dataPath, "gs1-company-prefixes.tsv")
				};
			}
		});
	}

	private Gs1BarcodesConsoleEntryPoint()
	{
	}
}
