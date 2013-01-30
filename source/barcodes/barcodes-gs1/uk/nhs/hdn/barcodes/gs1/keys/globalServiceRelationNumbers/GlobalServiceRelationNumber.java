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

package uk.nhs.hdn.barcodes.gs1.keys.globalServiceRelationNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.barcodes.Digits;
import uk.nhs.hdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

public final class GlobalServiceRelationNumber extends AbstractCheckDigitNumber<GlobalServiceRelationNumberFormat>
{
	public GlobalServiceRelationNumber(@NotNull final GlobalServiceRelationNumberFormat globalServiceRelationNumberFormat, @NotNull final Digits digits)
	{
		super(globalServiceRelationNumberFormat, digits);
	}
}
