/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.linesParsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;

import java.util.List;

public interface LinesParser<V>
{
	void parse(@MillisecondsSince1970 final long lastModified, @NotNull final List<V> lines);
}
