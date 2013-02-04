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

package uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.barcodes.gs1.checkDigits.IncorrectCheckDigitIllegalStateException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class DigitsAreNotForAGlobalTradeItemNumberException extends Exception
{
	public DigitsAreNotForAGlobalTradeItemNumberException(@NotNull final Digits digits)
	{
		super(message(digits));
	}

	public DigitsAreNotForAGlobalTradeItemNumberException(@NotNull final Digits digits, @NotNull final IncorrectCheckDigitIllegalStateException cause)
	{
		super(message(digits), cause);
	}

	private static String message(final Digits digits)
	{
		return format(ENGLISH, "Digits (%1$s) are not fot a Global Trade Item Number", digits);
	}
}
