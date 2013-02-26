package uk.nhs.hdn.ckan.client.relationships;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.api.ApiMethod;
import uk.nhs.hdn.ckan.api.RelationshipType;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.api.RelationshipType.values;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;

public final class CkanClientRelationshipsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientRelationshipsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.acceptsAll(enumAsLongOptions(values()), enumDescription(values(), false, true)).withRequiredArg().ofType(String.class).describedAs("a name or UUID");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final RelationshipType relationshipType = enumOptionChosen(optionSet, values());
		@NotNull final String key = required(optionSet, convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, relationshipType));

		// This potentially makes UUIDs behave as names - but this works due to the nature of the CKAN version 2 API. Done to avoid yet another command line switch
		execute(relationshipType, new DatasetName(key));
	}

	private static void execute(final RelationshipType relationshipType, final DatasetKey datasetKey) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final ApiMethod<DatasetId[]> apiMethod = DataGovUk.datasetRelationships(datasetKey, relationshipType);
		final DatasetId[] datasetIds = apiMethod.get();
		tsvSerialiserForDatasetIds().printValuesOnStandardOut(datasetIds);
	}
}
