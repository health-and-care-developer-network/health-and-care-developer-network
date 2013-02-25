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
import static uk.nhs.hdn.ckan.api.RelationshipType.depends_on;
import static uk.nhs.hdn.ckan.api.RelationshipType.values;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;

public final class CkanClientRelationshipConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String RelationshipOption = "relationship";
	private static final String KeyOption = "key";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientRelationshipConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(RelationshipOption, enumDescription(values(), false, false)).withRequiredArg().ofType(RelationshipType.class).defaultsTo(depends_on).describedAs("gets known relationships");
		// This potentially makes UUIDs behave as names - but this works due to the nature of the CKAN API. Done to avoid yet another command line switch
		options.accepts(KeyOption, "name or UUID").withRequiredArg().ofType(DatasetName.class).describedAs("a name or UUID");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final RelationshipType relationshipType = defaulted(optionSet, RelationshipOption);
		@NotNull final DatasetName datasetName = required(optionSet, KeyOption);

		execute(relationshipType, datasetName);
	}

	private static void execute(final RelationshipType relationshipType, final DatasetKey datasetKey) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final ApiMethod<DatasetId[]> apiMethod = DataGovUk.datasetRelationships(datasetKey, relationshipType);
		final DatasetId[] datasetIds = apiMethod.get();
		tsvSerialiserForDatasetIds().printValuesOnStandardOut(datasetIds);
	}
}
