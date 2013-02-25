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

package uk.nhs.hdn.ckan.client.dataset.search;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.Api;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.api.search.searchDelegates.SearchDelegate;
import uk.nhs.hdn.ckan.domain.Dataset;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.tuples.Pair;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.api.search.ManySearchCriteria.noSearchCriteria;
import static uk.nhs.hdn.ckan.api.search.StringSearchCriterion.*;
import static uk.nhs.hdn.ckan.domain.Dataset.*;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.tuples.Pair.pair;

public final class CkanClientDatasetSearchConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@NotNull @NonNls public static final String ReplacementForQ = "any";

	private static final Pair<String, String>[] FieldNames = of
	(
		pair(ReplacementForQ, "search in any field"),
		pair(titleField, "search in the title field"),
		pair(tagsField, "search in the tags field (a hyphenated tag)"),
		pair(notesField, "search in the notes field"),
		pair(groupsField, "search in the groups field (name or UUID)"),
		pair(authorField, "search in the author field"),
		pair(maintainerField, "search in the maintainer field"),
		pair(DatasetSearchUpdateFrequency, "search by update frequency (uncertain what this is)")
	);

	@NonNls @NotNull private static final String AsDatasetIdsOption = "as-dataset-ids";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientDatasetSearchConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		for (final Pair<String, String> fieldName : FieldNames)
		{
			options.accepts(convertUnderscoresInValueAsTheyAreNotValidForLongOptions(true, fieldName.a), fieldName.b).withRequiredArg().ofType(String.class).describedAs("substring to search for case insensitively");
			options.accepts(AsDatasetIdsOption, "returns results as Dataset Ids (UUIDs)").withOptionalArg().ofType(boolean.class).describedAs("true if as dataset ids;, false or unspecified to produce dataset names");
		}
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		SearchCriteria<Dataset> searchCriteria = noSearchCriteria();
		for (final Pair<String, String> fieldName : FieldNames)
		{
			final String optionName = fieldName.a;
			if (optionSet.has(optionName))
			{
				@NotNull final String substringToSearchFor = required(optionSet, optionName);
				final String key = optionName.equals(ReplacementForQ) ? DatasetSearchAny : reverseConversionOfUnderscoresInValueAsTheyAreNotValidForLongOptions(optionName);
				searchCriteria = searchCriteria.and(datasetSearchCriterion(key, substringToSearchFor));
			}
		}

		final boolean asDatasetIds = booleanOption(optionSet, AsDatasetIdsOption);

		execute(searchCriteria, asDatasetIds);
	}

	private static void execute(final SearchCriteria<Dataset> searchCriteria, final boolean asDatasetIds) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final SearchDelegate<? extends DatasetKey, Dataset> searchDelegate;
		final SeparatedValueSerialiser separatedValueSerialiser;
		final Api api = DataGovUk;
		if (asDatasetIds)
		{
			searchDelegate = api.datasetIdSearchDelegate();
			separatedValueSerialiser = tsvSerialiserForDatasetIds();
		}
		else
		{
			searchDelegate = api.datasetNameSearchDelegate();
			separatedValueSerialiser = tsvSerialiserForDatasetNames();
		}
		final DatasetKey[] datasetKeys = searchDelegate.allResults(searchCriteria);
		separatedValueSerialiser.printValuesOnStandardOut(datasetKeys);
	}
}
