/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.sources;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.CouldNotParseException;
import uk.nhs.hcdn.common.parsers.Parser;

import java.io.IOException;

public interface Source
{
	void load(@NotNull final Parser parser) throws IOException, CouldNotParseException;
}
