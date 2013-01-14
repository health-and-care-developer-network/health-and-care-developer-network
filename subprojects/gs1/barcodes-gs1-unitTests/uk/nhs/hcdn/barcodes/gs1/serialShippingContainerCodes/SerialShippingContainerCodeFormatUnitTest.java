/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.serialShippingContainerCodes;

import org.junit.Test;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;

import static junit.framework.TestCase.assertEquals;
import static uk.nhs.hcdn.barcodes.Digit.Five;
import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.barcodes.gs1.keys.serialShippingContainerCodes.SerialShippingContainerCodeFormat.SSCC;

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
