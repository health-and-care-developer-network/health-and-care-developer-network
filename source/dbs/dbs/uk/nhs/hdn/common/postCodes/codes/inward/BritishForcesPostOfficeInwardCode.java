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

public final class BritishForcesPostOfficeInwardCode extends AbstractCode implements InwardCode
{
	public BritishForcesPostOfficeInwardCode(@NotNull @NonNls final String oneToFourAlphanumericCharacters)
	{
		super(oneToFourAlphanumericCharacters);
		final int length = oneToFourAlphanumericCharacters.length();
		if (length < 1 || length > 4)
		{
			throw new IllegalArgumentException(format(ENGLISH, "inward code '%1$s' is not 1 - 4 characters (inclusive) in length", oneToFourAlphanumericCharacters));
		}

		for (int index = 0; index < length; index++)
		{
			if (isNumeric(oneToFourAlphanumericCharacters.charAt(index)))
			{
				throw new IllegalArgumentException(format(ENGLISH, "BFPO inward code '%1$s' contains non-numeric character at index %2$s", oneToFourAlphanumericCharacters, index));
			}
		}
	}
}
