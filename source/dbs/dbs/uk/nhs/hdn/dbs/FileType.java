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

package uk.nhs.hdn.dbs;

import org.jetbrains.annotations.Nullable;

public enum FileType
{
	Request(0),
	Response(1),
	;

	private static final class CompilerWorkaround
	{
		private static final FileType[] Index = new FileType[2];
	}

	public final int value;

	FileType(final int value)
	{
		this.value = value;
		CompilerWorkaround.Index[value] = this;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static FileType fileType(final int value)
	{
		if (value < 0 || value > 1)
		{
			return null;
		}
		return CompilerWorkaround.Index[value];
	}
}
