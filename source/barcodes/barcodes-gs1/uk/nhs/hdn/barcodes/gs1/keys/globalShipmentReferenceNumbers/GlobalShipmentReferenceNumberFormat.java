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
import uk.nhs.hdn.barcodes.gs1.Gs1ExtractingCheckDigitCalculator;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.DigitList;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.barcodes.gs1.Gs1ExtractingCheckDigitCalculator;
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
	private final Gs1ExtractingCheckDigitCalculator gs1CheckDigitCalculator;

	@SuppressWarnings("MagicNumber")
	GlobalShipmentReferenceNumberFormat(@NonNls @NotNull final String actualName, @NonNls @NotNull final String... formerActualNames)
	{
		this.actualName = actualName;
		gs1CheckDigitCalculator = new Gs1ExtractingCheckDigitCalculator
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
		gs1CheckDigitCalculator.guardCorrectNumberOfDigitsIfNoCheckDigit(digits);
	}

	@Override
	public void guardCorrectNumberOfDigits(@NotNull final Digits digits)
	{
		gs1CheckDigitCalculator.guardCorrectNumberOfDigits(digits);
	}

	@Override
	public void guardCheckDigitCorrect(@NotNull final Digits digits)
	{
		gs1CheckDigitCalculator.guardCheckDigitCorrect(digits);
	}

	@Override
	@NotNull
	public Digit calculateCheckDigit(@NotNull final Digits digits)
	{
		return gs1CheckDigitCalculator.calculateCheckDigit(digits);
	}

	@NotNull
	@Override
	public Digits addCheckDigit(@NotNull final Digits withoutCheckDigits)
	{
		return gs1CheckDigitCalculator.addCheckDigit(withoutCheckDigits);
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
