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

package uk.nhs.hdn.common.http.server.sun.helpers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.http.ContentTypeWithCharacterSet;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.Serialiser;

import java.io.ByteArrayOutputStream;

import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class ByteArrayResourceContentHelper
{
	private ByteArrayResourceContentHelper()
	{}

	@SafeVarargs
	public static <S extends MapSerialisable> ByteArrayResourceContent resourceContent(@ContentTypeWithCharacterSet @NonNls @NotNull final String contentType, @NotNull final Serialiser serialiser, final int guess, final S... values)
	{
		return new ByteArrayResourceContent(contentType, serialise(serialiser, guess, values));
	}

	@SafeVarargs
	private static <S extends MapSerialisable> byte[] serialise(@NotNull final Serialiser serialiser, final int guess, @NotNull final S... values)
	{
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(guess);
		try
		{
			serialiser.start(byteArrayOutputStream, Utf8);
			try
			{
				serialiser.writeValue(values);
			}
			catch (CouldNotWriteValueException e)
			{
				throw new ShouldNeverHappenException(e);
			}
			serialiser.finish();
		}
		catch (CouldNotWriteDataException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}
}
