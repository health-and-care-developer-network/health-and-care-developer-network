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

package uk.nhs.hdn.dbs.request.requestBodies;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.dbs.DbsDate;
import uk.nhs.hdn.dbs.LocalPatientIdentifier;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;
import static uk.nhs.hdn.dbs.RecordType.RequestBody;

public abstract class AbstractRequestBody implements RequestBody
{
	@FieldTokenName
	@NonNls
	@NotNull
	public static final String ResponseCodeField = "responseCode";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String LocalPatientIdentifierField = "localPatientIdentifier";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String DateOfBirthField = "dateOfBirth";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String DateOfDeathField = "dateOfDeath";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String OldNhsNumberField = "oldNhsNumber";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String NhsNumberField = "nhsNumber";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String FamilyNameField = "familyName";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAlternativeSurnameField = "previousAlternativeSurname";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String GivenNameField = "givenName";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAlternativeGivenNameField = "previousAlternativeGivenName";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AdministrativeGenderField = "administrativeGender";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AddressLine1Field = "addressLine1";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AddressLine2Field = "addressLine2";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AddressLine3Field = "addressLine3";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AddressLine4Field = "addressLine4";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String AddressLine5Field = "addressLine5";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PostCodeField = "postCode";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAddressLine1Field = "previousAddressLine1";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAddressLine2Field = "previousAddressLine2";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAddressLine3Field = "previousAddressLine3";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAddressLine4Field = "previousAddressLine4";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousAddressLine5Field = "previousAddressLine5";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousPostCodeField = "previousPostCode";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String RegistedGpField = "registeredGp";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String RegistedGpPracticeField = "registeredGpPractice";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousRegistedGpField = "previousRegisteredGp";

	@FieldTokenName
	@NonNls
	@NotNull
	public static final String PreviousRegistedGpPracticeField = "previousRegisteredGpPractice";

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForRequestBody(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForRequestBody(final boolean writeHeaderLine)
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		ResponseCodeField,
		LocalPatientIdentifierField,
		DateOfBirthField,
		DateOfDeathField,
		OldNhsNumberField,
		NhsNumberField,
		FamilyNameField,
		PreviousAlternativeSurnameField,
		GivenNameField,
		PreviousAlternativeGivenNameField,
		AdministrativeGenderField,
		AddressLine1Field,
		AddressLine2Field,
		AddressLine3Field,
		AddressLine4Field,
		AddressLine5Field,
		PostCodeField,
		PreviousAddressLine1Field,
		PreviousAddressLine2Field,
		PreviousAddressLine3Field,
		PreviousAddressLine4Field,
		PreviousAddressLine5Field,
		PreviousPostCodeField,
		RegistedGpField,
		RegistedGpPracticeField,
		PreviousRegistedGpField,
		PreviousRegistedGpPracticeField,
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(ResponseCodeField, 0),
		leaf(LocalPatientIdentifierField, 1),
		leaf(DateOfBirthField, 2),
		leaf(DateOfDeathField, 3),
		leaf(OldNhsNumberField, 4),
		leaf(NhsNumberField, 5),
		leaf(FamilyNameField, 6),
		leaf(PreviousAlternativeSurnameField, 7),
		leaf(GivenNameField, 8),
		leaf(PreviousAlternativeGivenNameField, 9),
		leaf(AdministrativeGenderField, 10),
		leaf(AddressLine1Field, 11),
		leaf(AddressLine2Field, 12),
		leaf(AddressLine3Field, 13),
		leaf(AddressLine4Field, 14),
		leaf(AddressLine5Field, 15),
		leaf(PostCodeField, 16),
		leaf(PreviousAddressLine1Field, 17),
		leaf(PreviousAddressLine2Field, 18),
		leaf(PreviousAddressLine3Field, 19),
		leaf(PreviousAddressLine4Field, 20),
		leaf(PreviousAddressLine5Field, 21),
		leaf(PreviousPostCodeField, 22),
		leaf(RegistedGpField, 23),
		leaf(RegistedGpPracticeField, 24),
		leaf(PreviousRegistedGpField, 25),
		leaf(PreviousRegistedGpPracticeField, 26)
	);

	@NotNull
	private final LocalPatientIdentifier localPatientIdentifier;
	@NotNull
	private final DbsDate dateOfBirth;

	protected AbstractRequestBody(@NotNull final LocalPatientIdentifier localPatientIdentifier, @NotNull final DbsDate dateOfBirth)
	{
		this.localPatientIdentifier = localPatientIdentifier;
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public final void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(ResponseCodeField, RequestBody);
			mapSerialiser.writeProperty(LocalPatientIdentifierField, localPatientIdentifier);
			mapSerialiser.writeProperty(DateOfBirthField, dateOfBirth);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractRequestBody that = (AbstractRequestBody) obj;

		if (!dateOfBirth.equals(that.dateOfBirth))
		{
			return false;
		}
		if (!localPatientIdentifier.equals(that.localPatientIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = localPatientIdentifier.hashCode();
		result = 31 * result + dateOfBirth.hashCode();
		return result;
	}
}
