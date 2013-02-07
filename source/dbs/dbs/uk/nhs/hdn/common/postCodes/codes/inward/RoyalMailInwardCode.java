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

package uk.nhs.hdn.common.postCodes.codes.inward;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.postCodes.codes.AbstractCode;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class RoyalMailInwardCode extends AbstractCode implements InwardCode
{
	public RoyalMailInwardCode(@NotNull @NonNls final String threeAlphanumericCharacters)
	{
		super(threeAlphanumericCharacters);
		final int length = threeAlphanumericCharacters.length();
		if (length != 3)
		{
			throw new IllegalArgumentException(format(ENGLISH, "inward code '%1$s' is not 3 characters (inclusive) in length", threeAlphanumericCharacters));
		}

		final char sector = threeAlphanumericCharacters.charAt(0);
		if (isNotNumeric(sector))
		{
			throw new IllegalArgumentException(format(ENGLISH, "inward code '%1$s' is not numeric at index 0", threeAlphanumericCharacters));
		}

		// Postcode Unit
		if (isNotValidAlphabeticPostcodeUnit(threeAlphanumericCharacters.charAt(1)))
		{
			throw new IllegalArgumentException(format(ENGLISH, "inward code '%1$s' is not alphabetic at index 1", threeAlphanumericCharacters));
		}
		if (isNotValidAlphabeticPostcodeUnit(threeAlphanumericCharacters.charAt(2)))
		{
			throw new IllegalArgumentException(format(ENGLISH, "inward code '%1$s' is not alphabetic at index 2", threeAlphanumericCharacters));
		}
	}
}
