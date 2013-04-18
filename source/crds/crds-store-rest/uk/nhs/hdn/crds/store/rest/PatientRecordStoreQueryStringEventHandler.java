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

package uk.nhs.hdn.crds.store.rest;

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

public final class PatientRecordStoreQueryStringEventHandler extends AbstractToString implements QueryStringEventHandler
{
	@NotNull
	public static PatientRecordStoreQueryStringEventHandler parseCrdsStoreQueryString(@Nullable final String rawQueryString) throws BadRequestException
	{
		final PatientRecordStoreQueryStringEventHandler queryStringEventHandler = new PatientRecordStoreQueryStringEventHandler();
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
	private boolean formatSeen;
	private boolean isXml;
	@Nullable
	private String callback;

	public PatientRecordStoreQueryStringEventHandler()
	{
		formatSeen = false;
		isXml = false;
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
	}

	public boolean isXml()
	{
		return isXml;
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
