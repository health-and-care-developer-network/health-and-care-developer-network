/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers;

import org.jetbrains.annotations.NotNull;

public interface JsonParseResultUser<V>
{
	void use(@NotNull final V value);
}
