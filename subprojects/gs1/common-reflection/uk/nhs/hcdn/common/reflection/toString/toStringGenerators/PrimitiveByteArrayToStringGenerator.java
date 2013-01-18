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

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;

import static java.lang.String.format;
import static java.util.Locale.UK;

public final class PrimitiveByteArrayToStringGenerator extends AbstractToStringGenerator<byte[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveByteArrayToStringGeneratorInstance = new PrimitiveByteArrayToStringGenerator();

	private PrimitiveByteArrayToStringGenerator()
	{
		super(byte[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final byte[] value)
	{
		final StringWriter stringWriter = new StringWriter(value.length * 4);
		byteArrayToHexString(stringWriter, (byte[]) value);
		return stringWriter.toString();
	}

	private static void byteArrayToHexString(@NotNull final StringWriter writer, @NotNull final byte... values)
	{
		writer.write("[");
		boolean afterFirst = false;
		@NonNls final String format = "0x%1$02X";
		for (final byte value : values)
		{
			if (afterFirst)
			{
				writer.write(", ");
			}
			afterFirst = true;
			writer.write(format(UK, format, value));
		}
		writer.write("]");
	}
}
