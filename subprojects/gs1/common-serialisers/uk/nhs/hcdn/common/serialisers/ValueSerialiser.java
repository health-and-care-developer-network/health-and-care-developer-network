/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ValueSerialiser
{
	@SuppressWarnings("MethodCanBeVariableArityMethod")
	<S extends MapSerialisable> void writeValue(@NotNull S[] values) throws CouldNotWriteValueException;

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	<S extends ValueSerialisable> void writeValue(@NotNull S[] values) throws CouldNotWriteValueException;

	void writeValue(int value) throws CouldNotWriteValueException;

	void writeValue(@NotNull final MapSerialisable value) throws CouldNotWriteValueException;

	void writeValue(@NotNull final ValueSerialisable value) throws CouldNotWriteValueException;

	void writeValue(@Nullable final Object value) throws CouldNotWriteValueException;

	void writeValueNull() throws CouldNotWriteValueException;
}
