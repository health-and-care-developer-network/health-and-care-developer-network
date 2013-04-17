/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.common.serialisers.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public final class JsonPSerialiser extends JsonSerialiser
{
	public static final int OpenBrace = (int) '(';
	@SuppressWarnings("PublicStaticArrayField") @NotNull public static final char[] Ending = ");\n".toCharArray();

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
