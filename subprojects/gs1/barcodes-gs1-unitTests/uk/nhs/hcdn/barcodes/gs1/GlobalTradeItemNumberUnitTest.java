package uk.nhs.hcdn.barcodes.gs1;

import org.junit.Assert;
import org.junit.Test;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;

import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_12;

public final class GlobalTradeItemNumberUnitTest
{
	// http://en.wikipedia.org/wiki/Universal_Product_Code
	// http://www.gs1.org/barcodes/support/check_digit_calculator
	// The GTIN is the same as the above less the final trailing 9
	private static final Digits ValidGtin12Number = digits("123456789999");

	// More prefixes: http://www.gs1.org/barcodes/support/prefix_list

	@Test
	public void testGtin12ExtractsTestDigit()
	{
		final GlobalTradeItemNumber globalTradeItemNumber = new GlobalTradeItemNumber(GTIN_12, ValidGtin12Number);
		final Digit checkDigit = globalTradeItemNumber.checkDigit();
		Assert.assertEquals("Check digit was not correct", Digit.Nine, checkDigit);
	}
}
