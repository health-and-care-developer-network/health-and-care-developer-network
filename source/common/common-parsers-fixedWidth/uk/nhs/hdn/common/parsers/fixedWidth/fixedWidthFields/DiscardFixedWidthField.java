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

package uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthFields;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.CouldNotParseException;

import java.io.IOException;
import java.io.Reader;

public final class DiscardFixedWidthField implements FixedWidthField
{
	private final int width;
	private final char[] discard;

	public DiscardFixedWidthField(final int width)
	{
		this.width = width;
		discard = new char[width];
	}

	@Override
	@NotNull
	public String parse(@NotNull final Reader bufferedReader) throws IOException, CouldNotParseException
	{
		int toRead = width;
		do
		{
			final int read = bufferedReader.read(discard);
			if (read == -1)
			{
				throw new CouldNotParseException(0, "End of input within discard fixed width field");
			}
			toRead -= read;
		} while(toRead != 0);

		return "";
	}
}
