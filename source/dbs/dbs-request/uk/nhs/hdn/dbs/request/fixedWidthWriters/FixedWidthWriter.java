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

package uk.nhs.hdn.dbs.request.fixedWidthWriters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;

import java.io.IOException;

public interface FixedWidthWriter
{
	void writeString(final int width, @NotNull final CharSequence value) throws IOException, CouldNotEncodeDataException;

	void writeUnsignedNumber(final int width, final int value) throws IOException, CouldNotEncodeDataException;

	void writeUnsignedNumber(final int width, final long value) throws IOException, CouldNotEncodeDataException;

	void writeLine() throws IOException;
}
