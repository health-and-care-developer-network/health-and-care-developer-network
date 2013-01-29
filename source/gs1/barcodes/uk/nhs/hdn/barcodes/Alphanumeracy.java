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

package uk.nhs.hdn.barcodes;

import org.jetbrains.annotations.NotNull;

public enum Alphanumeracy
{
	NoneAlphanumeric(0),
	FortyOneAlphanumeric(41),
	FortyEightAlphanumeric(48),
	Gs1DataMatrixAlphanumeric(2335),
	;

	private final int alphanumeracy;

	Alphanumeracy(final int alphanumeracy)
	{
		this.alphanumeracy = alphanumeracy;
	}

	public int numeracy()
	{
		return alphanumeracy;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return Integer.toString(alphanumeracy);
	}
}
