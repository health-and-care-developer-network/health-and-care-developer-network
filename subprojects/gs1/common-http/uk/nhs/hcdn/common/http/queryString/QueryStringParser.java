/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.queryString;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class QueryStringParser
{
	private static final int Equals = (int) '=';
	private static final int Ampersand = (int) '&';

	private QueryStringParser()
	{}

	public static void parseQueryString(@Nullable final String rawQueryString, @NotNull final QueryStringEventHandler queryStringEventHandler) throws InvalidQueryStringException
	{
		if (rawQueryString == null)
		{
			return;
		}

		boolean parsingKey = true;
		String key = null;
		int tokenStartsAtIndex = 0;
		final int length = rawQueryString.length();
		for(int index = 0; index < length; index++)
		{
			final int character = (int) rawQueryString.charAt(index);
			if (parsingKey)
			{
				switch (character)
				{
					case Equals:
						key = rawQueryString.substring(tokenStartsAtIndex, index);
						if (key.isEmpty())
						{
							throw new InvalidQueryStringException("there is an empty key");
						}
						tokenStartsAtIndex = index + 1;
						parsingKey = false;
						break;

					case Ampersand:
						throw new InvalidQueryStringException("a key contains an ampersand character");

					default:
						break;
				}
			}
			else
			{
				switch (character)
				{
					case Equals:
						throw new InvalidQueryStringException("a value contains an equals character");

					case Ampersand:
						final String value = rawQueryString.substring(tokenStartsAtIndex, index);
						try
						{
							queryStringEventHandler.keyValuePair(key, value);
						}
						catch (InvalidQueryStringKeyValuePairException e)
						{
							throw new InvalidQueryStringException(e);
						}
						tokenStartsAtIndex = index + 1;
						parsingKey = true;
						break;

					default:
						break;
				}
			}
		}

		if (parsingKey)
		{
			if (tokenStartsAtIndex != length)
			{
				throw new InvalidQueryStringException("the query string ends with a key");
			}
		}
		else
		{
			final String value = rawQueryString.substring(tokenStartsAtIndex, length);
			try
			{
				queryStringEventHandler.keyValuePair(key, value);
			}
			catch (InvalidQueryStringKeyValuePairException e)
			{
				throw new InvalidQueryStringException(e);
			}
		}
		queryStringEventHandler.validate();
	}
}
