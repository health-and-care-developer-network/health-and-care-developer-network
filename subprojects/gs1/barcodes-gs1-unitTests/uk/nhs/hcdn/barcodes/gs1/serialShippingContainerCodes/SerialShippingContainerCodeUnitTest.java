/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
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
