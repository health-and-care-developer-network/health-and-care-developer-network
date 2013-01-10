package uk.nhs.hcdn.barcodes.gs1;

import org.junit.Test;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;

import static junit.framework.TestCase.assertEquals;
import static uk.nhs.hcdn.barcodes.Digit.*;
import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

public final class GlobalTradeItemNumberFormatUnitTest
{
	@Test
	public void gtin8CalculatesCorrectCheckDigit()
	{
		final Digits withoutCheckDigit = digits("0421184");
		final Digit digit = GTIN_8.calculateCheckDigit(withoutCheckDigit);
		assertEquals(Six, digit);
	}

	@Test
	public void gtin12CalculatesCorrectCheckDigit()
	{
		final Digits withoutCheckDigit = digits("12345678998");
		final Digit digit = GTIN_12.calculateCheckDigit(withoutCheckDigit);
		assertEquals(Two, digit);
	}

	@Test
	public void gtin13CalculatesCorrectCheckDigit()
	{
		final Digits withoutCheckDigit = digits("112345678999");
		final Digit digit = GTIN_13.calculateCheckDigit(withoutCheckDigit);
		assertEquals(Eight, digit);
	}

	@Test
	public void gtin14CalculatesCorrectCheckDigit()
	{
		final Digits withoutCheckDigit = digits("4112345678991");
		final Digit digit = GTIN_14.calculateCheckDigit(withoutCheckDigit);
		assertEquals(Zero, digit);
	}
}
