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

package uk.nhs.hdn.dbs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.EnumSet.noneOf;
import static uk.nhs.hdn.dbs.DataReturnedInAdditionalResponse.*;

// Also known as Response Code and DBS Code
public enum RecordType
{
	Header("00"),
	RequestBody("10"),
	Trailer("99"),
	SuccessSingleMatchAlphanumericTrace("20", false, TracedDetails, true),
	NoMatchAlphanumericTrace("21", false, Nothing, false),
	MultipleMatchAlphanumericTrace("22", false, Nothing, true),
	SuccessNhsNumberFoundAndVerifiedInCrossCheck("30", true, TracedDetails, true),
	FailedAtCrossCheckAndWentToAlphanumericTraceNoMatchFoundInAlphanumericTrace("31", true, Nothing, false),
	FailedAtCrossCheckAndWentToAlphanumericTraceTheAlphanumericCriteriaMatchMultipleRecords("32", true, Nothing, true),
	SuccessFailedAtCrossCheckAndWentToAlphanumericTraceSingleMatchFound("33", true, TracedDetailsMayHaveADifferentNhsNumber, true),
	SuccessFailedAtCrossCheckAndWentToAlphanumericTraceTheNhsNumberHasBeenMergedWithAnotheRecord("40", true, TracedDetailsIncludingReplacementNhsNumber, true),
	InsufficientDataToPerformSearch("81", null, Nothing, false),
	Timeout("82", null, Nothing, false), // If there is a large volume of Code 82 then the NHS CFH Service Desk should be alerted as there may be problems with the trace service.
	;

	private static final class CompilerWorkaround
	{
		private static final Map<String, RecordType> Index = new HashMap<>(3);
	}

	@NotNull
	public final String recordType;
	@Nullable
	public final Boolean nhsNumberInRequest;
	@Nullable
	private final DataReturnedInAdditionalResponse dataReturnedInAdditionalResponse;
	public final boolean hasMatches;
	public final boolean doesNotHaveMatches;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	RecordType(@NotNull final String recordType)
	{
		this(recordType, null, null, false);
	}

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	RecordType(@NotNull final String recordType, @Nullable final Boolean nhsNumberInRequest, @Nullable final DataReturnedInAdditionalResponse dataReturnedInAdditionalResponse, final boolean hasMatches)
	{
		this.recordType = recordType;
		this.nhsNumberInRequest = nhsNumberInRequest;
		this.dataReturnedInAdditionalResponse = dataReturnedInAdditionalResponse;
		this.hasMatches = hasMatches;
		doesNotHaveMatches = !hasMatches;
		if (CompilerWorkaround.Index.put(recordType, this) != null)
		{
			throw new IllegalArgumentException("duplicate recordType");
		}
	}

	public boolean hasNhsNumberinRequest()
	{
		return nhsNumberInRequest != null && nhsNumberInRequest;
	}

	public boolean isValidForResponseBody()
	{
		return dataReturnedInAdditionalResponse != null;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static RecordType recordType(@NotNull final String value)
	{
		return CompilerWorkaround.Index.get(value);
	}

	@NotNull
	public static Set<RecordType> validRecordTypesForResponseBody()
	{
		final Set<RecordType> recordTypes = noneOf(RecordType.class);
		for (final RecordType value : values())
		{
			if (value.isValidForResponseBody())
			{
				recordTypes.add(value);
			}
		}
		return recordTypes;
	}
}
