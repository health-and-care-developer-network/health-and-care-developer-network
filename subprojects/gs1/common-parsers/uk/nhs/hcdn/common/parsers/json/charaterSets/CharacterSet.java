/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.charaterSets;

public interface CharacterSet
{
	boolean contains(final char value);

	boolean doesNotContain(final char value);
}
