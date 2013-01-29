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

package uk.nhs.hdn.barcodes.gs1.keys.globalShipmentReferenceNumbers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.barcodes.Digit;
import uk.nhs.hdn.barcodes.DigitList;
import uk.nhs.hdn.barcodes.Digits;
import uk.nhs.hdn.barcodes.gs1.checkDigits.CheckDigitCalculator;
import uk.nhs.hdn.barcodes.gs1.keys.KeyFormat;

import java.util.Set;

import static uk.nhs.hdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum GlobalShipmentReferenceNumberFormat implements KeyFormat
{
	GSRN("Global Shipment Reference Number"),
	;
	public static final int MaximumOneBasedPositionT = 17;

	@NotNull
	private final String actualName;
	private final Set<String> formerActualNames;
	private final CheckDigitCalculator checkDigitCalculator;

	@SuppressWarnings("MagicNumber")
	GlobalShipmentReferenceNumberFormat(@NonNls @NotNull final String actualName, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		checkDigitCalculator = new CheckDigitCalculator
		(
			MaximumOneBasedPositionT
		);
		this.formerActualNames = unmodifiableSetOf(formerActualNames);
	}

	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
	}

	@Override
	public void guardCorrectNumberOfDigitsIfNoCheckDigit(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCorrectNumberOfDigitsIfNoCheckDigit(digits);
	}

	@Override
	public void guardCorrectNumberOfDigits(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCorrectNumberOfDigits(digits);
	}

	@Override
	public void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		checkDigitCalculator.guardCheckDigitCorrect(digits);
	}

	@Override
	@NotNull
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		return checkDigitCalculator.calculateCheckDigit(digits);
	}

	@NotNull
	@Override
	public Digits addCheckDigit(@NotNull final Digits withoutCheckDigits)
	{
		return checkDigitCalculator.addCheckDigit(withoutCheckDigits);
	}

	@Override
	@NotNull
	public String toString()
	{
		return actualName();
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return formerActualNames;
	}

	@Override
	@NotNull
	public Digit extract(@NotNull final DigitList digits, final int oneBasedPositionT)
	{
		return digits.digitAtPositionT(oneBasedPositionT);
	}

	@Override
	public int size()
	{
		return MaximumOneBasedPositionT;
	}
}
