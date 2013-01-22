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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.naming.ActualName;

import java.util.HashMap;
import java.util.Map;

public enum Version implements ActualName
{
	VersionMajor1Minor0("1.0"),
	;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final Map<String, Version> Index = new HashMap<>(1);
	}

	@NonNls
	@NotNull
	private final String actualName;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	Version(@NonNls @NotNull final String actualName)
	{
		this.actualName = actualName;
		CompilerWorkaround.Index.put(name(), this);
	}

	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@NotNull
	@SuppressWarnings("RefusedBequest")
	@Override
	public String toString()
	{
		return actualName;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static Version version(@NonNls @NotNull final String value)
	{
		return CompilerWorkaround.Index.get(value);
	}
}
