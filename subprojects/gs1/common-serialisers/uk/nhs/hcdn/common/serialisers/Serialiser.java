/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.nio.charset.Charset;

public interface Serialiser extends ValueSerialiser, MapSerialiser
{
	void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException;

	void finish() throws CouldNotWriteDataException;
}
