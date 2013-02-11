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

package uk.nhs.hdn.common.postCodes.codes.outward;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.common.postCodes.RoyalMailPostCode;
import uk.nhs.hdn.common.postCodes.codes.AbstractCode;
import uk.nhs.hdn.common.postCodes.codes.inward.RoyalMailInwardCode;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;

public final class RoyalMailOutwardCode extends AbstractCode implements OutwardCode<RoyalMailPostCode, RoyalMailInwardCode>
{
	private static final String Giro = "GIR";
	private static final String Santa = "SAN";
	private static final Collection<String> ThreeLetterExceptions = new HashSet<>(asList(Giro, Santa));
	private static final Collection<String> FourLetterExceptions = new HashSet<>(asList("ASCN", "BBND", "BIQQ", "FIQQ", "PCRN", "SIQQ", "STHL", "TDCU", "TKCA"));

	public RoyalMailOutwardCode(@NotNull @NonNls final String twoToFourAlphanumericCharacters)
	{
		super(twoToFourAlphanumericCharacters);
		switch(twoToFourAlphanumericCharacters.length())
		{
			case 2:
				validateTwoLetterPostCode(twoToFourAlphanumericCharacters);
				break;

			case 3:
				validateThreeLetterPostCode(twoToFourAlphanumericCharacters);
				break;

			case 4:
				validateFourLetterPostCode(twoToFourAlphanumericCharacters);
				break;

			default:
				throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' is not 2 - 4 characters (inclusive) in length", twoToFourAlphanumericCharacters));
		}
	}

	@NotNull
	@Override
	public RoyalMailPostCode with(@NotNull final RoyalMailInwardCode inwardCode)
	{
		return new RoyalMailPostCode(this, inwardCode);
	}

	private static void validateTwoLetterPostCode(final String value)
	{
		if (isNotAlphabetic(value.charAt(0)) && isNotNumeric(value.charAt(1)))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' is not Alphabetic then Numeric", value));
		}
	}

	@SuppressWarnings("FeatureEnvy")
	private static void validateThreeLetterPostCode(final String value)
	{
		if (ThreeLetterExceptions.contains(value))
		{
			return;
		}
		if (PostCode.ValidAlphabeticCharacters.doesNotContain(value.charAt(0)))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' contains invalid character at index 0", value));
		}
		final char char1 = value.charAt(1);
		final char char2 = value.charAt(2);
		if (isAlphabetic(char1))
		{
			if (isNotNumeric(char2))
			{
				throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' contains non-numeric character at index 2", value));
			}
			return;
		}
		if (isNotNumeric(char1))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' contains non-numeric character at index 1", value));
		}
		if (isNumeric(char2))
		{
			return;
		}
		if (isNotTrailingAlphabetic(char2))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' contains invalid character at index 2", value));
		}
	}

	private static void validateFourLetterPostCode(final String value)
	{
		if (FourLetterExceptions.contains(value))
		{
			return;
		}

		if (isNotAlphabetic(value.charAt(0)) && isNotAlphabetic(value.charAt(1)) && isNotNumeric(value.charAt(2)))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' is not Alphabetic then Alphabetic then Numeric", value));
		}

		final char char3 = value.charAt(3);
		if (isNumeric(char3))
		{
			return;
		}
		if (isNotTrailingAlphabetic(char3))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Outward code '%1$s' is not Alphabetic then Alphabetic then Numeric then Trailing Alphabetic", value));
		}
	}
}
