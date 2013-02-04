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

package uk.nhs.hdn.barcodes.gs1.serialShippingContainerCodes;

import org.junit.Test;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;

import static junit.framework.TestCase.assertEquals;
import static uk.nhs.hdn.common.digits.Digit.Five;
import static uk.nhs.hdn.common.digits.Digits.digits;
import static uk.nhs.hdn.barcodes.gs1.keys.serialShippingContainerCodes.SerialShippingContainerCodeFormat.SSCC;

public final class SerialShippingContainerCodeFormatUnitTest
{
	@Test
	public void sscnCalculatesCorrectCheckDigit()
	{
		final Digits withoutCheckDigit = digits("12345678901234567");
		final Digit digit = SSCC.calculateCheckDigit(withoutCheckDigit);
		assertEquals(Five, digit);
	}
}
