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
import org.jetbrains.annotations.Nullable;
import org.openhealthtools.ihe.bridge.type.*;
import uk.nhs.hdn.ihe.xds.builders.abstractions.AbstractBuilder;
import uk.nhs.hdn.ihe.xds.builders.abstractions.Builder;
import uk.nhs.hdn.number.NhsNumber;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.hdn.ihe.xds.builders.PatientIdBuilder.patientId;
import static uk.nhs.hdn.ihe.xds.builders.PhoneBuilder.phoneType;

public final class PatientInfoBuilder extends AbstractBuilder<PatientInfoType>
{
	@NotNull
	public static PatientInfoBuilder patientInfo(@NotNull final Builder<PatientNameType> name)
	{
		return patientInfo(name.build());
	}

	@NotNull
	public static PatientInfoBuilder patientInfo(@NotNull final PatientNameType name)
	{
		return new PatientInfoBuilder(name);
	}

	@NotNull private final PatientNameType name;
	@Nullable private PatientIdType identifier;
	@Nullable @NonNls private String dateOfBirth;
	@Nullable private PatientSex sex;
	@Nullable private AddressType address;
	@Nullable private PhoneType homePhone;
	@Nullable private PhoneType workPhone;
	@NotNull private final List<GenericAdtValue> genericAdtValues;

	public PatientInfoBuilder(@NotNull final PatientNameType name)
	{
		this.name = name;
		identifier = null;
		dateOfBirth = null;
		sex = null;
		address = null;
		homePhone = null;
		workPhone = null;
		genericAdtValues = new ArrayList<>(10);
	}

	@NotNull
	public PatientInfoBuilder dateOfBirth(@NotNull @NonNls final String dateOfBirth)
	{
		if (this.dateOfBirth != null)
		{
			throw new IllegalStateException("dateOfBirth already specified");
		}
		this.dateOfBirth = dateOfBirth;
		return this;
	}

	@NotNull
	public PatientInfoBuilder identifier(@NotNull @NonNls final String nhsNumber)
	{
		return identifier(patientId(nhsNumber));
	}

	@NotNull
	public PatientInfoBuilder identifier(@NotNull @NonNls final NhsNumber nhsNumber)
	{
		return identifier(patientId(nhsNumber));
	}

	@NotNull
	public PatientInfoBuilder identifier(@NotNull @NonNls final PatientIdType identifier)
	{
		if (this.identifier != null)
		{
			throw new IllegalStateException("identifier already specified");
		}
		this.identifier = identifier;
		return this;
	}

	@NotNull
	public PatientInfoBuilder sex(@NotNull final PatientSex sex)
	{
		if (this.sex != null)
		{
			throw new IllegalStateException("sex already specified");
		}
		this.sex = sex;
		return this;
	}

	@NotNull
	public PatientInfoBuilder address(@NotNull final Builder<AddressType> address)
	{
		return address(address.build());
	}

	@NotNull
	public PatientInfoBuilder address(@NotNull final AddressType address)
	{
		if (this.address != null)
		{
			throw new IllegalStateException("address already specified");
		}
		this.address = address;
		return this;
	}

	@NotNull
	public PatientInfoBuilder homePhone(@NotNull @NonNls final String unformattedTelephoneNumber)
	{
		return homePhone(phoneType(unformattedTelephoneNumber));
	}

	@NotNull
	public PatientInfoBuilder homePhone(@NotNull final PhoneType telephoneNumber)
	{
		if (homePhone != null)
		{
			throw new IllegalStateException("homePhone already specified");
		}
		homePhone = telephoneNumber;
		return this;
	}

	@NotNull
	public PatientInfoBuilder workPhone(@NotNull @NonNls final String unformattedTelephoneNumber)
	{
		return workPhone(phoneType(unformattedTelephoneNumber));
	}

	@NotNull
	public PatientInfoBuilder workPhone(@NotNull final PhoneType telephoneNumber)
	{
		if (workPhone != null)
		{
			throw new IllegalStateException("workPhone already specified");
		}
		workPhone = telephoneNumber;
		return this;
	}

	@SuppressWarnings("MethodWithMoreThanThreeNegations")
	@NotNull
	@Override
	public PatientInfoType build()
	{
		final PatientInfoType patientInfo = new PatientInfoType();
		patientInfo.setPatientName(name);
		if (dateOfBirth != null)
		{
			patientInfo.setPatientDateOfBirth(dateOfBirth);
		}
		if (identifier != null)
		{
			patientInfo.setPatientIdentifier(identifier);
		}
		if (sex != null)
		{
			patientInfo.setPatientSex(sex.iheSex);
		}
		if (address != null)
		{
			patientInfo.setPatientAddress(address);
		}
		if (homePhone != null)
		{
			patientInfo.setPatientPhoneHome(homePhone);
		}
		if (workPhone != null)
		{
			patientInfo.setPatientPhoneHome(workPhone);
		}
		final int size = genericAdtValues.size();
		if (size != 0)
		{
			patientInfo.setGenericAdtValues(genericAdtValues.toArray(new GenericAdtValue[size]));
		}

		return patientInfo;
	}
}
