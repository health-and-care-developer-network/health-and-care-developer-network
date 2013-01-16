/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.queryString;

import org.jetbrains.annotations.NotNull;

public interface QueryStringEventHandler
{
	void keyValuePair(@NotNull final String key, @NotNull final String value) throws InvalidQueryStringKeyValuePairException;

	void validate() throws InvalidQueryStringException;
}
