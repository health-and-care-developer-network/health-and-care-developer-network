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

package uk.nhs.hdn.common.postCodes.codes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public abstract class AbstractCode extends AbstractToString implements Code
{
	@NotNull
	@NonNls
	private final String code;

	protected AbstractCode(@NotNull @NonNls final String code)
	{
		this.code = code;
	}

	public static boolean isNotAlphabetic(final char character)
	{
		return PostCode.ValidAlphabeticCharacters.doesNotContain(character);
	}

	public static boolean isNotNumeric(final char character)
	{
		return PostCode.ValidNumericCharacters.doesNotContain(character);
	}

	public static boolean isAlphabetic(final char character)
	{
		return PostCode.ValidAlphabeticCharacters.contains(character);
	}

	public static boolean isNumeric(final char character)
	{
		return PostCode.ValidNumericCharacters.contains(character);
	}

	public static boolean isNotTrailingAlphabetic(final char character)
	{
		return PostCode.ValidTrailingAlphabeticCharacters.doesNotContain(character);
	}

	public static boolean isNotValidAlphabeticPostcodeUnit(final char character)
	{
		return PostCode.ValidAlphabeticPostcodeUnitCharacters.doesNotContain(character);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractCode that = (AbstractCode) obj;

		if (!code.equals(that.code))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return code.hashCode();
	}

	@Override
	@NotNull
	public String normalised()
	{
		return code;
	}
}
