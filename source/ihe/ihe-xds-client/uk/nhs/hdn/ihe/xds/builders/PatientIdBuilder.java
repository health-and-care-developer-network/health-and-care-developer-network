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

package uk.nhs.hdn.ihe.xds.builders;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.openhealthtools.ihe.bridge.type.PatientIdType;
import uk.nhs.hdn.number.NhsNumber;

import static uk.nhs.hdn.number.NhsNumber.valueOf;

public final class PatientIdBuilder
{
	@NotNull
	public static PatientIdType patientId(@NotNull @NonNls final String nhsNumber)
	{
		return patientId(valueOf(nhsNumber));
	}

	@NotNull
	public static PatientIdType patientId(@SuppressWarnings("TypeMayBeWeakened") @NotNull final NhsNumber nhsNumber)
	{
		return new PatientIdType(nhsNumber.normalised());
	}

	private PatientIdBuilder()
	{
	}
}
