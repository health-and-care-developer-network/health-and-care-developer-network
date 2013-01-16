/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;

public interface SeparatedValueParseEventHandler
{
	void start(@MillisecondsSince1970 final long lastModified);

	void field(@NotNull final String value) throws CouldNotParseFieldException;

	void endOfLine() throws CouldNotParseLineException;

	void end();
}
