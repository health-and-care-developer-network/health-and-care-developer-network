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

package uk.nhs.hdn.ckan.client.resource.search;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.domain.Resource;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.tuples.Pair;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.api.search.ManySearchCriteria.noSearchCriteria;
import static uk.nhs.hdn.ckan.api.search.StringSearchCriterion.resourceSearchCriterion;
import static uk.nhs.hdn.ckan.domain.Resource.*;
import static uk.nhs.hdn.ckan.domain.ids.ResourceId.tsvSerialiserForResourceIds;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.tuples.Pair.pair;

public final class CkanClientResourceSearchConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final Pair<String, String>[] FieldNames = of
	(
		pair(urlField, "search in the url field"),
		pair(descriptionField, "search in the description field"),
		pair(formatField, "search in the format field (stick to file extensions)")
	);

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientResourceSearchConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		for (final Pair<String, String> fieldName : FieldNames)
		{
			options.accepts(convertUnderscoresInValueAsTheyAreNotValidForLongOptions(true, fieldName.a), fieldName.b).withRequiredArg().ofType(String.class).describedAs("substring to search for case insensitively");
		}
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		SearchCriteria<Resource> searchCriteria = noSearchCriteria();
		for (final Pair<String, String> fieldName : FieldNames)
		{
			final String optionName = fieldName.a;
			if (optionSet.has(optionName))
			{
				@NotNull final String substringToSearchFor = required(optionSet, optionName);
				final String key = reverseConversionOfUnderscoresInValueAsTheyAreNotValidForLongOptions(optionName);
				searchCriteria = searchCriteria.and(resourceSearchCriterion(key, substringToSearchFor));
			}
		}
		execute(searchCriteria);
	}

	private static void execute(@NotNull final SearchCriteria<Resource> searchCriteria) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final ResourceId[] results = DataGovUk.resourceSearchDelegate().allResults(searchCriteria);
		tsvSerialiserForResourceIds().printValuesOnStandardOut(results);
	}
}
