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

package uk.nhs.hcdn.dts.domain;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.dts.domain.BooleanFlag;

import static uk.nhs.hcdn.dts.domain.BooleanFlag.N;
import static uk.nhs.hcdn.dts.domain.BooleanFlag.Y;

// Data can not be both compressed and encrypted
public enum ContentEncoding
{
	AsIs(N, N),
	Compressed(Y, N),
	Encrypted(N, Y),
	;

	@NotNull
	public final BooleanFlag compression;
	@NotNull
	public final BooleanFlag encryption;

	ContentEncoding(@NotNull final BooleanFlag compression, @NotNull final BooleanFlag encryption)
	{
		this.compression = compression;
		this.encryption = encryption;
	}
}
