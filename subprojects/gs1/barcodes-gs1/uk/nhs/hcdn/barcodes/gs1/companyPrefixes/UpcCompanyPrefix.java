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

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

import static uk.nhs.hcdn.barcodes.Digit.Zero;

public final class UpcCompanyPrefix extends AbstractCompanyPrefix
{
	private static final Digits DigitsZero = new Digits(false, Zero);

	public UpcCompanyPrefix(@NotNull final Digits digits)
	{
		super(digits);
	}

	@NotNull
	public Gs1CompanyPrefix toGs1CompanyPrefix()
	{
		return new Gs1CompanyPrefix(DigitsZero.add(digits));
	}
}
