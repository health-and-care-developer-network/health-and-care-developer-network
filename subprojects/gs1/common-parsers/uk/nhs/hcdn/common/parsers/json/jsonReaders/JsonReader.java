/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonReaders;

import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;

public interface JsonReader
{
	long position();

	char readRequiredCharacter() throws InvalidJsonException;

	char peekCharacter() throws EndOfFileException;

	char readCharacter() throws EndOfFileException;

	void pushBackLastCharacter();
}
