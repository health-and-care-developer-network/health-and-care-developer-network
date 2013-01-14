/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

public interface Serialiser extends ValueSerialiser, MapSerialiser
{
	void start() throws CouldNotWriteDataException;

	void finish() throws CouldNotWriteDataException;
}
