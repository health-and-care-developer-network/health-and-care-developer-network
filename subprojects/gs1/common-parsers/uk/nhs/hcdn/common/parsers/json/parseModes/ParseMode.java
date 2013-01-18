/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.JsonReader;

public interface ParseMode
{
	void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException;
}
