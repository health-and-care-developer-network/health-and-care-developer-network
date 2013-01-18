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

package uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers;

import org.junit.Test;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.DigitsCanNotbeCompressedException;

import static junit.framework.TestCase.assertEquals;
import static uk.nhs.hcdn.barcodes.Digit.*;
import static uk.nhs.hcdn.barcodes.Digits.digits;
import static uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

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

	@Test
	public void gtin8ToGtin12()
	{
		final Digits gtin12 = upcEToUpcA(digits("03614504"));
		assertEquals(digits("036000001457"), gtin12);
	}

	// Known to be broken - nothing can be done about it without better documentation
	// try http://www.morovia.com/education/utility/upc-ean.asp#upca  or  http://www.nepc.gs1.org.sg/html/DataDictionary/EANCode.htm#_Toc463160351
	@Test
	public void gtin12ToGtin8() throws DigitsCanNotbeCompressedException
	{
		final Digits gtin8 = upcAToUpcE(digits("036000001457"));
		assertEquals(digits("03614504"), gtin8);
	}
}
