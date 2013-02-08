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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;
import uk.nhs.hdn.dbs.LocalPatientIdentifier;
import uk.nhs.hdn.dbs.RecordType;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.recurse;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;
import static uk.nhs.hdn.dbs.response.practiceAndAddressData.KnownPracticeAndAddressData.*;

public abstract class AbstractResponseBody extends AbstractIsUnknown implements ResponseBody
{
	@FieldTokenName
	@NonNls
	@NotNull public static final String ResponseCodeField = "responseCode";
	@FieldTokenName @NonNls
	@NotNull public static final String LocalPatientIdentifierField = "localPatientIdentifier";
	@FieldTokenName @NonNls
	@NotNull public static final String NumberOfMultipleMatchesField = "numberOfMultipleMatchesField";
	@FieldTokenName @NonNls
	@NotNull public static final String TraceResultNhsNumberField = "traceResultNhsNumberField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedDateOfBirthField = "returnedDateOfBirthField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedFactOfDeathField = "returnedFactOfDeathField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedDateOfDeathField = "returnedDateOfDeathField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedFamilyNameField = "returnedFamilyNameField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedGivenNameField = "returnedGivenNameField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedAlternativeOrPreviousSurnameField = "returnedAlternativeOrPreviousSurnameField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedOtherGivenNamesField = "returnedOtherGivenNamesField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedGenderField = "returnedGenderField";
	@FieldTokenName @NonNls
	@NotNull public static final String ReturnedPracticeAndAddressDataField = "returnedPracticeAndAddressDataField";

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForResponseBody(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForResponseBody(final boolean writeHeaderLine)
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		ResponseCodeField,
		LocalPatientIdentifierField,
		NumberOfMultipleMatchesField,
		TraceResultNhsNumberField,
		ReturnedDateOfBirthField,
		ReturnedFactOfDeathField,
		ReturnedDateOfDeathField,
		ReturnedFamilyNameField,
		ReturnedGivenNameField,
		ReturnedAlternativeOrPreviousSurnameField,
		ReturnedOtherGivenNamesField,
		ReturnedGenderField,
		ReturnedPracticeAndAddressDataField
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(ResponseCodeField, 0),
		leaf(LocalPatientIdentifierField, 1),
		leaf(NumberOfMultipleMatchesField, 2),
		leaf(TraceResultNhsNumberField, 3),
		leaf(ReturnedDateOfBirthField, 4),
		leaf(ReturnedFactOfDeathField, 5),
		leaf(ReturnedDateOfDeathField, 6),
		leaf(ReturnedFamilyNameField, 7),
		leaf(ReturnedGivenNameField, 8),
		leaf(ReturnedAlternativeOrPreviousSurnameField, 9),
		leaf(ReturnedOtherGivenNamesField, 10),
		leaf(ReturnedGenderField, 11),
		recurse(ReturnedPracticeAndAddressDataField,
			leaf(ReturnedAddressLine1Field, 12),
			leaf(ReturnedAddressLine2Field, 13),
			leaf(ReturnedAddressLine3Field, 14),
			leaf(ReturnedAddressLine4Field, 15),
			leaf(ReturnedAddressLine5Field, 16),
			leaf(ReturnedPostCodeField, 17),
			leaf(ReturnedRegisteredGpPracticeCodeField, 18)
		)
	);

	@NotNull
	public final RecordType responseCode;
	@NotNull
	public final LocalPatientIdentifier localPatientIdentifier;
	public final int numberOfMultipleMatches;

	protected AbstractResponseBody(@NotNull final RecordType responseCode, @NotNull final LocalPatientIdentifier localPatientIdentifier, final int numberOfMultipleMatches)
	{
		super(responseCode.doesNotHaveMatches);
		this.responseCode = responseCode;
		this.localPatientIdentifier = localPatientIdentifier;
		this.numberOfMultipleMatches = numberOfMultipleMatches;
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
			mapSerialiser.writeProperty(ResponseCodeField, responseCode);
			mapSerialiser.writeProperty(LocalPatientIdentifierField, localPatientIdentifier);
			mapSerialiser.writeProperty(NumberOfMultipleMatchesField, numberOfMultipleMatches);
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
		if (!super.equals(obj))
		{
			return false;
		}

		final AbstractResponseBody that = (AbstractResponseBody) obj;

		if (numberOfMultipleMatches != that.numberOfMultipleMatches)
		{
			return false;
		}
		if (!localPatientIdentifier.equals(that.localPatientIdentifier))
		{
			return false;
		}
		if (responseCode != that.responseCode)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + responseCode.hashCode();
		result = 31 * result + localPatientIdentifier.hashCode();
		result = 31 * result + numberOfMultipleMatches;
		return result;
	}
}
