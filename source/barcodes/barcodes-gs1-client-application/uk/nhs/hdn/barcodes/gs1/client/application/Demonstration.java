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

package uk.nhs.hdn.barcodes.gs1.client.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.client.CachingClientApplication;
import uk.nhs.hdn.barcodes.gs1.client.ClientApplication;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;

import static java.lang.System.out;
import static uk.nhs.hdn.barcodes.gs1.organisation.Tuple.tsvSerialiserForTuples;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class Demonstration extends AbstractToString
{
	public static void demonstrateClientApplication(final boolean cache, @Nullable final CharSequence gtin, @NotNull final ClientApplication clientApplication) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		final SeparatedValueSerialiser separatedValueSerialiser = tsvSerialiserForTuples();

		if (gtin == null)
		{
			final Tuple[] tuples = clientApplication.listAllKnownCompanyPrefixes();
			printTuples(separatedValueSerialiser, tuples);
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
			printTuples(separatedValueSerialiser, tuple);
		}
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void printTuples(final SeparatedValueSerialiser separatedValueSerialiser, final Tuple... tuples)
	{
		try
		{
			separatedValueSerialiser.start(out, Utf8);
			separatedValueSerialiser.writeValue(tuples);
			separatedValueSerialiser.finish();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new IllegalStateException("Could not write tuples", e);
		}
	}
}
