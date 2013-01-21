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

package uk.nhs.hcdn.barcodes.gs1.server.application;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.server.Gs1CompanyPrefixRestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RootDenialRestEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories.RestEndpointsFactory;

import java.io.File;

import static uk.nhs.hcdn.common.http.server.sun.application.ServerApplication.start;

public final class Gs1BarcodesServerConsoleEntryPoint
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
					new RootDenialRestEndpoint(),
					new Gs1CompanyPrefixRestEndpoint(dataPath, "gs1-company-prefixes.tsv")
				};
			}
		});
	}

	private Gs1BarcodesServerConsoleEntryPoint()
	{
	}
}
