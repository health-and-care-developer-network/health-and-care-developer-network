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

package uk.nhs.hdn.ihe.xds.builders;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public enum PatientSex
{
	Male('M'),
	Female('F'),
	Other('O'),
	Unknown('U'),
	Ambiguous('A'),
	NotApplicable('N'),
	;

	private static final class CompilerWorkaround
	{
		private static final Map<String, PatientSex> Index = new HashMap<>(6);
	}

	@NotNull @NonNls public final String iheSex;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	PatientSex(final char character)
	{
		iheSex = new String(new char[] {character});
		if (CompilerWorkaround.Index.put(iheSex, this) != null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Duplicate sex code %1$s", iheSex));
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static PatientSex patientSex(@NotNull @NonNls final String iheSex)
	{
		@Nullable final PatientSex patientSex = CompilerWorkaround.Index.get(iheSex);
		if (patientSex == null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Invalid IHE sex code %1$s", iheSex));
		}

		return patientSex;
	}
}
