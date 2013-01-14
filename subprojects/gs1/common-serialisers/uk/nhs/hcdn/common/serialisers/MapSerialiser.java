/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MapSerialiser
{
	void writeProperty(@NonNls @NotNull String name, @NotNull String value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @NotNull MapSerialisable value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @NotNull ValueSerialisable value) throws CouldNotWritePropertyException;

	void writeProperty(@NonNls @NotNull String name, @Nullable Object value) throws CouldNotWritePropertyException;

	void writePropertyNull(@NonNls @NotNull String name) throws CouldNotWritePropertyException;
}
