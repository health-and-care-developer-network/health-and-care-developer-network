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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.DigitsAreNotForAGlobalTradeItemNumberException;
import uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumber;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.barcodes.gs1.organisation.index.Index;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.barcodes.Digits.digits;
import static uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumber.parseGlobalTradeItemNumber;

// Could be more sophisticated, using the cache-control and expires headers to deduce when to flush the cache
public final class CachingClientApplication
{
	private final Index cache;

	public CachingClientApplication(@NotNull final ClientApplication clientApplication) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		final Tuple[] tuples = clientApplication.listAllKnownCompanyPrefixes();
		cache = new Index(tuples);
	}

	@NotNull
	public Tuple listCompanyPrefixForGlobalTradeItemNumberFromCache(@NotNull final CharSequence digits)
	{
		final GlobalTradeItemNumber globalTradeItemNumber;
		try
		{
			globalTradeItemNumber = parseGlobalTradeItemNumber(digits(digits), true);
		}
		catch (DigitsAreNotForAGlobalTradeItemNumberException e)
		{
			throw new IllegalArgumentException("Supplied GTIN is incorrect", e);
		}
		final Gs1CompanyPrefixAndItem gsCompanyPrefixAndItem = globalTradeItemNumber.gs1CompanyPrefixAndItem();
		@Nullable final Tuple tuple = cache.find(gsCompanyPrefixAndItem);
		if (tuple == null)
		{
			throw new IllegalArgumentException("Supplied GTIN does not have a known company prefix");
		}
		return tuple;
	}
}
