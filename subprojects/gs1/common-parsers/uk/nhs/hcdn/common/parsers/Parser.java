/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;

import java.io.BufferedReader;
import java.io.IOException;

public interface Parser
{
	void parse(@NotNull final BufferedReader bufferedReader, @MillisecondsSince1970 final long lastModified) throws IOException, CouldNotParseException;
}
