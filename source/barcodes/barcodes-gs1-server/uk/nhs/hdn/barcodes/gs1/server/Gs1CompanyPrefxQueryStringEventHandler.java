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
	private static final String JsonPAndXmlAreIncompatible = "jsonp and xml are incompatible";
	private static final String JsonPAndTsvAreIncompatible = "jsonp and tsv are incompatible";
	private static final String JsonPAndCsvAreIncompatible = "jsonp and csv are incompatible";
	private boolean formatSeen;
	private boolean isXml;
	private boolean isTsv;
	private boolean isCsv;
	@Nullable
	private String jsonp;

	public Gs1CompanyPrefxQueryStringEventHandler()
	{
		formatSeen = false;
		isXml = false;
		isTsv = false;
		isCsv = false;
		jsonp = null;
	}

	@Override
	public void keyValuePair(@NonNls @NotNull final String key, @NonNls @NotNull final String value) throws InvalidQueryStringKeyValuePairException
	{
		if ("format".equals(key))
		{
			if (formatSeen)
			{
				throw new InvalidQueryStringKeyValuePairException(key, value, "only one value of format is permitted");
			}
			formatSeen = true;
			if ("json".equals(value))
			{
				return;
			}
			if ("xml".equals(value))
			{
				isXml = true;
			}
			if ("tsv".equals(value))
			{
				isTsv = true;
			}
			if ("csv".equals(value))
			{
				isCsv = true;
			}
			return;
		}

		if ("jsonp".equals(key))
		{
			if (formatSeen)
			{
				if (isXml)
				{
					throw new InvalidQueryStringKeyValuePairException(key, value, JsonPAndXmlAreIncompatible);
				}
				if (isTsv)
				{
					throw new InvalidQueryStringKeyValuePairException(key, value, JsonPAndTsvAreIncompatible);
				}
				if (isCsv)
				{
					throw new InvalidQueryStringKeyValuePairException(key, value, JsonPAndCsvAreIncompatible);
				}
			}
			try
			{
				jsonp = validateJsonPFunctionName(value);
			}
			catch (JsonFunctionNameInvalidException e)
			{
				throw new InvalidQueryStringKeyValuePairException(key, value, e);
			}
			return;
		}

		throw new InvalidQueryStringKeyValuePairException(key, value, "the key is unrecognised");
	}

	public boolean isJsonP()
	{
		return jsonp != null;
	}

	@Override
	public void validate() throws InvalidQueryStringException
	{
		if (isXml && isJsonP())
		{
			throw new InvalidQueryStringException(JsonPAndXmlAreIncompatible);
		}
		if (isTsv && isJsonP())
		{
			throw new InvalidQueryStringException(JsonPAndTsvAreIncompatible);
		}
		if (isCsv && isJsonP())
		{
			throw new InvalidQueryStringException(JsonPAndCsvAreIncompatible);
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
	public String jsonp()
	{
		if (jsonp == null)
		{
			throw new IllegalStateException("Please call isJsonP() first");
		}
		return jsonp;
	}

	public boolean isFormatSeen()
	{
		return formatSeen;
	}

	public void setFormatSeen(final boolean formatSeen)
	{
		this.formatSeen = formatSeen;
	}
}
