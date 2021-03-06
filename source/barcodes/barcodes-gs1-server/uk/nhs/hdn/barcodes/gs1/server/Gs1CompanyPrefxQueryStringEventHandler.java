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

package uk.nhs.hdn.barcodes.gs1.server;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.http.queryString.InvalidQueryStringException;
import uk.nhs.hdn.common.http.queryString.InvalidQueryStringKeyValuePairException;
import uk.nhs.hdn.common.http.queryString.QueryStringEventHandler;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.json.JsonFunctionNameInvalidException;

import static uk.nhs.hdn.common.http.queryString.QueryStringParser.parseQueryString;
import static uk.nhs.hdn.common.serialisers.json.JsonPFunctionNameValidator.validateJsonPFunctionName;

public final class Gs1CompanyPrefxQueryStringEventHandler extends AbstractToString implements QueryStringEventHandler
{
	@NotNull
	public static Gs1CompanyPrefxQueryStringEventHandler parseGs1QueryString(@Nullable final String rawQueryString) throws BadRequestException
	{
		final Gs1CompanyPrefxQueryStringEventHandler queryStringEventHandler = new Gs1CompanyPrefxQueryStringEventHandler();
		try
		{
			parseQueryString(rawQueryString, queryStringEventHandler);
		}
		catch (InvalidQueryStringException e)
		{
			throw new BadRequestException(e.getMessage(), e);
		}
		return queryStringEventHandler;
	}

	@NotNull
	private static final String CallbackAndXmlAreIncompatible = "callback and xml are incompatible";
	private static final String CallbackAndTsvAreIncompatible = "callback and tsv are incompatible";
	private static final String CallbackAndCsvAreIncompatible = "callback and csv are incompatible";
	private boolean formatSeen;
	private boolean isXml;
	private boolean isTsv;
	private boolean isCsv;
	@Nullable
	private String callback;

	public Gs1CompanyPrefxQueryStringEventHandler()
	{
		formatSeen = false;
		isXml = false;
		isTsv = false;
		isCsv = false;
		callback = null;
	}

	@SuppressWarnings("HardCodedStringLiteral")
	@Override
	public void keyValuePair(@NonNls @NotNull final String key, @NonNls @NotNull final String value) throws InvalidQueryStringKeyValuePairException
	{
		switch(key)
		{
			case "format":
				formatKey(key, value);
				return;

			case "callback":
				callbackKey(key, value);
				return;

			default:
				throw new InvalidQueryStringKeyValuePairException(key, value, "the key is unrecognised");
		}
	}

	@SuppressWarnings("HardCodedStringLiteral")
	private void formatKey(final String key, final String value) throws InvalidQueryStringKeyValuePairException
	{
		if (formatSeen)
		{
			throw new InvalidQueryStringKeyValuePairException(key, value, "only one value of format is permitted");
		}
		formatSeen = true;
		switch (value)
		{
			case "json":
				break;

			case "xml":
				isXml = true;
				break;

			case "tsv":
				isTsv = true;
				break;

			case "csv":
				isCsv = true;
				break;

			default:
				break;
		}
	}

	private void callbackKey(final String key, final String value) throws InvalidQueryStringKeyValuePairException
	{
		if (formatSeen)
		{
			if (isXml)
			{
				throw new InvalidQueryStringKeyValuePairException(key, value, CallbackAndXmlAreIncompatible);
			}
			if (isTsv)
			{
				throw new InvalidQueryStringKeyValuePairException(key, value, CallbackAndTsvAreIncompatible);
			}
			if (isCsv)
			{
				throw new InvalidQueryStringKeyValuePairException(key, value, CallbackAndCsvAreIncompatible);
			}
		}
		try
		{
			callback = validateJsonPFunctionName(value);
		}
		catch (JsonFunctionNameInvalidException e)
		{
			throw new InvalidQueryStringKeyValuePairException(key, value, e);
		}
	}

	public boolean isJsonP()
	{
		return callback != null;
	}

	@Override
	public void validate() throws InvalidQueryStringException
	{
		if (isXml && isJsonP())
		{
			throw new InvalidQueryStringException(CallbackAndXmlAreIncompatible);
		}
		if (isTsv && isJsonP())
		{
			throw new InvalidQueryStringException(CallbackAndTsvAreIncompatible);
		}
		if (isCsv && isJsonP())
		{
			throw new InvalidQueryStringException(CallbackAndCsvAreIncompatible);
		}
	}

	public boolean isXml()
	{
		return isXml;
	}

	public boolean isTsv()
	{
		return isTsv;
	}

	public boolean isCsv()
	{
		return isCsv;
	}

	@NotNull
	public String jsonpFunctionName()
	{
		if (callback == null)
		{
			throw new IllegalStateException("Please call isJsonP() first");
		}
		return callback;
	}
}
