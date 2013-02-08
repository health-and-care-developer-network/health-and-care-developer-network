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

package uk.nhs.hdn.dbs.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.dbs.*;
import uk.nhs.hdn.dbs.response.practiceAndAddressData.PracticeAndAddressData;
import uk.nhs.hdn.number.NhsNumber;

import java.io.StringWriter;
import java.util.List;

import static uk.nhs.hdn.common.serialisers.AbstractSerialiser.writeNullableProperty;

public final class KnownResponseBody extends AbstractResponseBody
{
	private static final int Space = (int) ' ';
	private static final int MaximumSizeOfOtherGivenNames = 35;

	@NotNull
	public final NhsNumber traceResultNhsNumber;
	@Nullable
	public final DbsDate returnedDateOfBirth;
	@Nullable
	public final FactOfDeath returnedFactOfDeath;
	@Nullable
	public final DbsDate returnedDateOfDeath;
	@Nullable
	public final NameFragment returnedFamilyName;
	@Nullable
	public final NameFragment returnedGivenName;
	@Nullable
	public final NameFragment returnedAlternativeOrPreviousSurname;
	@NotNull
	public final List<NameFragment> returnedOtherGivenNames;
	@NotNull
	public final Gender returnedGender;
	@NotNull
	public final PracticeAndAddressData practiceAndAddressData;

	@SuppressWarnings("ConstructorWithTooManyParameters")
	public KnownResponseBody(@NotNull final RecordType responseCode, @NotNull final LocalPatientIdentifier localPatientIdentifier, final int numberOfMultipleMatches, @NotNull final NhsNumber traceResultNhsNumber, @Nullable final DbsDate returnedDateOfBirth, @Nullable final FactOfDeath returnedFactOfDeath, @Nullable final DbsDate returnedDateOfDeath, @Nullable final NameFragment returnedFamilyName, @Nullable final NameFragment returnedGivenName, @Nullable final NameFragment returnedAlternativeOrPreviousSurname, @NotNull final List<NameFragment> returnedOtherGivenNames, @NotNull final Gender returnedGender, @NotNull final PracticeAndAddressData practiceAndAddressData)
	{
		super(responseCode, localPatientIdentifier, numberOfMultipleMatches);
		this.traceResultNhsNumber = traceResultNhsNumber;
		this.returnedDateOfBirth = returnedDateOfBirth;
		this.returnedFactOfDeath = returnedFactOfDeath;
		this.returnedDateOfDeath = returnedDateOfDeath;
		this.returnedFamilyName = returnedFamilyName;
		this.returnedGivenName = returnedGivenName;
		this.returnedAlternativeOrPreviousSurname = returnedAlternativeOrPreviousSurname;
		//noinspection AssignmentToCollectionOrArrayFieldFromParameter
		this.returnedOtherGivenNames = returnedOtherGivenNames;
		this.returnedGender = returnedGender;
		this.practiceAndAddressData = practiceAndAddressData;
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		super.serialiseMap(mapSerialiser);
		try
		{
			mapSerialiser.writeProperty(TraceResultNhsNumberField, traceResultNhsNumber);
			writeNullableProperty(mapSerialiser, ReturnedDateOfBirthField, returnedDateOfBirth);
			writeNullableProperty(mapSerialiser, ReturnedFactOfDeathField, returnedFactOfDeath);
			writeNullableProperty(mapSerialiser, ReturnedDateOfDeathField, returnedDateOfDeath);
			writeNullableProperty(mapSerialiser, ReturnedFamilyNameField, returnedFamilyName);
			writeNullableProperty(mapSerialiser, ReturnedGivenNameField, returnedGivenName);
			writeNullableProperty(mapSerialiser, ReturnedAlternativeOrPreviousSurnameField, returnedAlternativeOrPreviousSurname);
			mapSerialiser.writeProperty(ReturnedOtherGivenNamesField, otherGivenNamesValue());
			mapSerialiser.writeProperty(ReturnedGenderField, returnedGender);
			mapSerialiser.writeProperty(ReturnedPracticeAndAddressDataField, practiceAndAddressData);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	private String otherGivenNamesValue()
	{
		final StringWriter writer = new StringWriter(MaximumSizeOfOtherGivenNames);
		boolean afterFirst = false;
		for (final NameFragment returnedOtherGivenName : returnedOtherGivenNames)
		{
			if (afterFirst)
			{
				writer.write(Space);
			}
			else
			{
				afterFirst = true;
			}
			writer.write(returnedOtherGivenName.value());
		}
		return writer.toString();
	}

	@SuppressWarnings({"ConditionalExpression", "FeatureEnvy", "OverlyComplexMethod"})
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
		if (!super.equals(obj))
		{
			return false;
		}

		final KnownResponseBody that = (KnownResponseBody) obj;

		if (!practiceAndAddressData.equals(that.practiceAndAddressData))
		{
			return false;
		}
		if (returnedAlternativeOrPreviousSurname != null ? !returnedAlternativeOrPreviousSurname.equals(that.returnedAlternativeOrPreviousSurname) : that.returnedAlternativeOrPreviousSurname != null)
		{
			return false;
		}
		if (returnedDateOfBirth != null ? !returnedDateOfBirth.equals(that.returnedDateOfBirth) : that.returnedDateOfBirth != null)
		{
			return false;
		}
		if (returnedDateOfDeath != null ? !returnedDateOfDeath.equals(that.returnedDateOfDeath) : that.returnedDateOfDeath != null)
		{
			return false;
		}
		if (returnedFactOfDeath != that.returnedFactOfDeath)
		{
			return false;
		}
		if (returnedFamilyName != null ? !returnedFamilyName.equals(that.returnedFamilyName) : that.returnedFamilyName != null)
		{
			return false;
		}
		if (returnedGender != that.returnedGender)
		{
			return false;
		}
		if (returnedGivenName != null ? !returnedGivenName.equals(that.returnedGivenName) : that.returnedGivenName != null)
		{
			return false;
		}
		if (!returnedOtherGivenNames.equals(that.returnedOtherGivenNames))
		{
			return false;
		}
		if (!traceResultNhsNumber.equals(that.traceResultNhsNumber))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({"UnclearBinaryExpression", "ConditionalExpression", "FeatureEnvy", "MethodWithMoreThanThreeNegations"})
	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + traceResultNhsNumber.hashCode();
		result = 31 * result + (returnedDateOfBirth != null ? returnedDateOfBirth.hashCode() : 0);
		result = 31 * result + (returnedFactOfDeath != null ? returnedFactOfDeath.hashCode() : 0);
		result = 31 * result + (returnedDateOfDeath != null ? returnedDateOfDeath.hashCode() : 0);
		result = 31 * result + (returnedFamilyName != null ? returnedFamilyName.hashCode() : 0);
		result = 31 * result + (returnedGivenName != null ? returnedGivenName.hashCode() : 0);
		result = 31 * result + (returnedAlternativeOrPreviousSurname != null ? returnedAlternativeOrPreviousSurname.hashCode() : 0);
		result = 31 * result + returnedOtherGivenNames.hashCode();
		result = 31 * result + returnedGender.hashCode();
		result = 31 * result + practiceAndAddressData.hashCode();
		return result;
	}
}
