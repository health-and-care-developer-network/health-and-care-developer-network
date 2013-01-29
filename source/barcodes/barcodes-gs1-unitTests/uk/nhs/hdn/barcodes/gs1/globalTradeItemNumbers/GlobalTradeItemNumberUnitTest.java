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

package uk.nhs.hdn.barcodes.gs1.globalTradeItemNumbers;

import org.junit.Test;
import uk.nhs.hdn.barcodes.Digit;
import uk.nhs.hdn.barcodes.Digits;
import uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumber;

import static org.junit.Assert.assertEquals;
import static uk.nhs.hdn.barcodes.Digit.*;
import static uk.nhs.hdn.barcodes.Digits.digits;
import static uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

public final class GlobalTradeItemNumberUnitTest
{
	private static final GlobalTradeItemNumber ValidGtin8Number = new GlobalTradeItemNumber(GTIN_8, digits("04211846"));
	private static final Digits NormalisedGlobalTradeItemNumberDigitsForGtin8 = digits("00000004211846");
	private static final GlobalTradeItemNumber ValidGtin12Number = new GlobalTradeItemNumber(GTIN_12, digits("123456789999"));
	private static final Digits NormalisedGlobalTradeItemNumberDigitsForGtin12 = digits("00123456789999");
	private static final GlobalTradeItemNumber ValidGtin13Number = new GlobalTradeItemNumber(GTIN_13, digits("1123456789998"));
	private static final Digits NormalisedGlobalTradeItemNumberDigitsForGtin13 = digits("01123456789998");
	private static final GlobalTradeItemNumber ValidGtin14Number = new GlobalTradeItemNumber(GTIN_14, digits("41123456789910"));
	private static final Digits NormalisedGlobalTradeItemNumberDigitsForGtin14 = digits("41123456789910");

	@Test
	public void gtin8ExtractsIndicatorDigit()
	{
		final Digit indicatorDigit = ValidGtin8Number.indicatorDigit();
		assertEquals(Zero, indicatorDigit);
	}

	@Test
	public void gtin8ExtractsCheckDigit()
	{
		final Digit checkDigit = ValidGtin8Number.checkDigit();
		assertEquals(Six, checkDigit);
	}

	@Test
	public void gtin8NormalisesToCorrectGlobalTradeItemNumber()
	{
		final Digits digits = ValidGtin8Number.normalise();
		assertEquals(NormalisedGlobalTradeItemNumberDigitsForGtin8, digits);
	}

	@Test
	public void gtin12ExtractsIndicatorDigit()
	{
		final Digit indicatorDigit = ValidGtin12Number.indicatorDigit();
		assertEquals(Zero, indicatorDigit);
	}

	@Test
	public void gtin12ExtractsCheckDigit()
	{
		final Digit checkDigit = ValidGtin12Number.checkDigit();
		assertEquals(Nine, checkDigit);
	}

	@Test
	public void gtin12NormalisesToCorrectGlobalTradeItemNumber()
	{
		final Digits digits = ValidGtin12Number.normalise();
		assertEquals(NormalisedGlobalTradeItemNumberDigitsForGtin12, digits);
	}

	@Test
	public void gtin13ExtractsIndicatorDigit()
	{
		final Digit indicatorDigit = ValidGtin13Number.indicatorDigit();
		assertEquals(Zero, indicatorDigit);
	}

	@Test
	public void gtin13ExtractsCheckDigit()
	{
		final Digit checkDigit = ValidGtin13Number.checkDigit();
		assertEquals(Eight, checkDigit);
	}

	@Test
	public void gtin13NormalisesToCorrectGlobalTradeItemNumber()
	{
		final Digits digits = ValidGtin13Number.normalise();
		assertEquals(NormalisedGlobalTradeItemNumberDigitsForGtin13, digits);
	}

	@Test
	public void gtin14ExtractsIndicatorDigit()
	{
		final Digit indicatorDigit = ValidGtin14Number.indicatorDigit();
		assertEquals(Four, indicatorDigit);
	}

	@Test
	public void gtin14ExtractsCheckDigit()
	{
		final Digit checkDigit = ValidGtin14Number.checkDigit();
		assertEquals(Zero, checkDigit);
	}

	@Test
	public void gtin14NormalisesToCorrectGlobalTradeItemNumber()
	{
		final Digits digits = ValidGtin14Number.normalise();
		assertEquals(NormalisedGlobalTradeItemNumberDigitsForGtin14, digits);
	}
}
