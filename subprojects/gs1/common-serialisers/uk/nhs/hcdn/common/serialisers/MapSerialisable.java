/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

public interface MapSerialisable
{
	void serialiseMap(@NotNull MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException;
}
