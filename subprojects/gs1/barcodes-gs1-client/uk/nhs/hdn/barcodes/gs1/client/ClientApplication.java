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

package uk.nhs.hdn.barcodes.gs1.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hdn.barcodes.gs1.client.HttpClientHelper.*;

public final class ClientApplication extends AbstractToString
{
	private final boolean useHttps;
	@NotNull
	private final String hostname;
	private final char portNumber;
	@NotNull
	private final HttpClient allHttpClient;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public ClientApplication(final boolean useHttps, @NotNull final String hostname, final char portNumber)
	{
		this.useHttps = useHttps;
		this.hostname = hostname;
		this.portNumber = portNumber;

		allHttpClient = forListAllKnownCompanyPrefixes(useHttps, hostname, portNumber);
	}

	@NotNull
	public Tuple[] listAllKnownCompanyPrefixes() throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		return allHttpClient.get(ResponseUser);
	}

	@NotNull
	public Tuple listCompanyPrefixForGlobalTradeItemNumber(@NotNull final CharSequence digits) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		return forACompanyPrefix(useHttps, hostname, portNumber, digits).get(ResponseUser)[0];
	}
}
