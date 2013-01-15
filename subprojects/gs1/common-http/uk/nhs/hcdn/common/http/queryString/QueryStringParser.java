/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.queryString;

import org.jetbrains.annotations.Nullable;

public final class QueryStringParser
{
	private static final int Equals = (int) '=';
	private static final int Ampersand = (int) '&';

	private QueryStringParser()
	{}

	public static void parse(@Nullable final String queryString)
	{
		if (queryString == null)
		{
			return;
		}

		boolean parsingKey = true;
		String key = null;
		int tokenStartsAtIndex = 0;
		for(int index = 0; index < queryString.length(); index+)
		{
			final int character = (int) queryString.charAt(index);
			if (parsingKey)
			{
				if (character == Equals)
				{
					key = queryString.substring(tokenStartsAtIndex, index);
					if (key.isEmpty())
					{
						throw new InvalidQueryStringException("Empty key");
					}
					tokenStartsAtIndex = index + 1;
					parsingKey = false;
				}
				else if (character == Ampersand)
				{
					throw new InvalidQueryStringException("Key contains ampersand");
				}
			}
			else
			{
				if (character == Ampersand)
				{
					final String value = queryString.substring(tokenStartsAtIndex, index);
					tokenStartsAtIndex = index + 1;
					parsingKey = true;
				}
				else if (character == Equals)
				{
					throw new InvalidQueryStringException("Value contains equals");
				}
			}
		}
	}
}
