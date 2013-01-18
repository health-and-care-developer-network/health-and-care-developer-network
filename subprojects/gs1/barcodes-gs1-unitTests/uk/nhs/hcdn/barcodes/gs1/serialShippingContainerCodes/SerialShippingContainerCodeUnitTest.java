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

package uk.nhs.hcdn.barcodes.gs1.serialShippingContainerCodes;

import org.junit.Test;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.gs1.keys.serialShippingContainerCodes.SerialShippingContainerCode;

import static org.junit.Assert.assertEquals;
import static uk.nhs.hcdn.barcodes.Digit.Five;
import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.barcodes.gs1.keys.serialShippingContainerCodes.SerialShippingContainerCodeFormat.SSCC;

public final class SerialShippingContainerCodeUnitTest
{
	@Test
	public void gtin8ExtractsCheckDigit()
	{
		@SuppressWarnings("TypeMayBeWeakened") final SerialShippingContainerCode sscn = new SerialShippingContainerCode(SSCC, digits("123456789012345675"));
		final Digit checkDigit = sscn.checkDigit();
		assertEquals(Five, checkDigit);
	}
}
