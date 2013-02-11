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

package uk.nhs.hdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.Digits;

public abstract class AbstractWithSerialComponentCheckDigitNumber<F extends SerialComponentKeyFormat> extends AbstractKeyFormatCheckDigitNumber<F>
{
	protected AbstractWithSerialComponentCheckDigitNumber(@SuppressWarnings("TypeMayBeWeakened") @NotNull final F serialComponentKeyFormat, @NotNull final Digits digits)
	{
		super(serialComponentKeyFormat, digits);
	}

	@NotNull
	public Digits serialComponent()
	{
		return digits.slice(extractCheckDigitCalculator.size(), digits.size());
	}
}
