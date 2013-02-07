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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum FactOfDeath
{
	PatientDeceased("D"),
	;

	private static final class CompilerWorkaround
	{
		private static final Map<String, FactOfDeath> Index = new HashMap<>(1);
	}

	@NotNull @NonNls
	public final String code;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	FactOfDeath(@NotNull @NonNls final String code)
	{
		this.code = code;
		if (CompilerWorkaround.Index.put(code, this) != null)
		{
			throw new IllegalArgumentException("duplicate code");
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static FactOfDeath factOfDatch(@NotNull final String value)
	{
		return CompilerWorkaround.Index.get(value);
	}
}
