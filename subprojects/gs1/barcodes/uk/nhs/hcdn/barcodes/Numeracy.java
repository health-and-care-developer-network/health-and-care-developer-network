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

package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;

public enum Numeracy
{
	NoneNumeric(0),
	EightNumeric(8),
	TwelveNumeric(12),
	ThirteenNumeric(13),
	FourteenNumeric(14),
	SeventyFourNumeric(74),
	Gs1DataMatrixNumeric(3116),
	;

	private final int numeracy;

	Numeracy(final int numeracy)
	{
		this.numeracy = numeracy;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public int numeracy()
	{
		return numeracy;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return Integer.toString(numeracy);
	}
}
