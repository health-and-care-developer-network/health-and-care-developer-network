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

package uk.nhs.hcdn.barcodes.gs1.server;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.queryString.InvalidQueryStringException;
import uk.nhs.hcdn.common.http.queryString.InvalidQueryStringKeyValuePairException;
import uk.nhs.hcdn.common.http.queryString.QueryStringEventHandler;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.json.JsonFunctionNameInvalidException;

import static uk.nhs.hcdn.common.http.queryString.QueryStringParser.parseQueryString;
import static uk.nhs.hcdn.common.serialisers.json.JsonPFunctionNameValidator.validateJsonPFunctionName;

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
	private boolean formatSeen;
	private boolean isXml;
	@Nullable
	private String jsonp;

	public Gs1CompanyPrefxQueryStringEventHandler()
	{
		formatSeen = false;
		isXml = false;
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
	}

	public boolean isXml()
	{
		return isXml;
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
}
