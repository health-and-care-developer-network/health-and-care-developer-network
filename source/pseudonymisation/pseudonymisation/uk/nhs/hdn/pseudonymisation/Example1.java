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

package uk.nhs.hdn.pseudonymisation;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.number.NhsNumber;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.HashPseudonymiser;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import static java.lang.System.out;
import static uk.nhs.hdn.common.MessageDigestHelper.SHA512;
import static uk.nhs.hdn.number.NhsNumber.valueOf;

public class Example1
{
	public void example()
	{
		final NhsNumber nhsNumber = valueOf("1234567881");

		final IndexTable<NhsNumber> nhsNumberMapIndexTable = new MapIndexTable<>();
		final Pseudonymiser<NhsNumber> pseudonymiser = new HashPseudonymiser<>(SHA512);
		pseudonymiser.pseudonymise(nhsNumber, nhsNumberMapIndexTable);

		nhsNumberMapIndexTable.iterate(new PsuedonymisedValuesUser<NhsNumber, ShouldNeverHappenException>()
		{
			@SuppressWarnings("UseOfSystemOutOrSystemErr")
			@Override
			public boolean use(@NotNull final NhsNumber valueToPsuedonymise, @NotNull final PsuedonymisedValues<NhsNumber> pseudonymisedValues)
			{
				out.print(valueToPsuedonymise.normalised());
				out.print(pseudonymisedValues.get(pseudonymiser));
				return true;
			}
		});
	}
}
