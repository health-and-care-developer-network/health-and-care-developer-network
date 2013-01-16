/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteDataException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public final class JsonPSerialiser extends JsonSerialiser
{
	private static final int OpenBrace = (int) '(';
	private static final char[] Ending = ");\n".toCharArray();

	@NotNull
	private final String jsonPPrefix;

	public JsonPSerialiser(@NotNull final String jsonPPrefix)
	{
		if (jsonPPrefix.isEmpty())
		{
			throw new IllegalArgumentException("jsonPPrefix must be at least one character");
		}
		this.jsonPPrefix = jsonPPrefix;
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		try
		{
			writer.write(jsonPPrefix);
			writer.write(OpenBrace);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@Override
	public void finish() throws CouldNotWriteDataException
	{
		try
		{
			writer.write(Ending);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
		finally
		{
			super.finish();
		}
	}
}
