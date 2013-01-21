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

package uk.nhs.hcdn.barcodes.gs1.client.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.gs1.client.CachingClientApplication;
import uk.nhs.hcdn.barcodes.gs1.client.ClientApplication;
import uk.nhs.hcdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hcdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hcdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hcdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public class Demonstration extends AbstractToString
{
	public static void demonstrateClientApplication(final boolean cache, @Nullable final CharSequence gtin, @NotNull final ClientApplication clientApplication) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		if (gtin == null)
		{
			final Tuple[] tuples = clientApplication.listAllKnownCompanyPrefixes();
			for (final Tuple tuple : tuples)
			{
				printTuple(tuple);
			}
		}
		else
		{
			final Tuple tuple;
			if (cache)
			{
				tuple = new CachingClientApplication(clientApplication).listCompanyPrefixForGlobalTradeItemNumberFromCache(gtin);
			}
			else
			{
				tuple = clientApplication.listCompanyPrefixForGlobalTradeItemNumber(gtin);
			}
			printTuple(tuple);
		}
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printTuple(final Tuple tuple)
	{
		System.out.println(tuple);
	}
}
