package uk.nhs.hdn.ckan.client.relationships;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.ckan.api.RelationshipType;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.api.RelationshipType.values;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;

public final class HdnCkanRelationshipsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@NotNull @NonNls private static final String AsDatasetIdsOption = "as-dataset-ids";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnCkanRelationshipsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		oneOfEnumAsOptionWithRequiredArgument(options, values(), DatasetName.class);
		options.accepts(AsDatasetIdsOption, "returns results as Dataset Ids (UUIDs)").withOptionalArg().ofType(boolean.class).describedAs("true if as dataset ids; false or unspecified to produce dataset names");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final RelationshipType relationshipType = enumOptionChosen(optionSet, values());
		// This potentially makes UUIDs behave as names - but this works due to the nature of the CKAN version 2 API. Done to avoid yet another command line switch
		@NotNull final DatasetName key = required(optionSet, convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, relationshipType));

		final boolean asDatasetIds = booleanOption(optionSet, AsDatasetIdsOption);

		execute(relationshipType, key, asDatasetIds);
	}

	private static void execute(final RelationshipType relationshipType, final DatasetName key, final boolean asDatasetIds) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final ApiMethod<? extends DatasetKey[]> apiMethod;
		final SeparatedValueSerialiser separatedValueSerialiser;
		if (asDatasetIds)
		{
			apiMethod = DataGovUk.datasetRelationshipsByDatasetId(key, relationshipType);
			separatedValueSerialiser = tsvSerialiserForDatasetIds();
		}
		else
		{
			apiMethod = DataGovUk.datasetRelationshipsByDatasetName(key, relationshipType);
			separatedValueSerialiser = tsvSerialiserForDatasetNames();
		}
		final DatasetKey[] datasetKeys = apiMethod.get();
		separatedValueSerialiser.printValuesOnStandardOut(datasetKeys);
	}
}
