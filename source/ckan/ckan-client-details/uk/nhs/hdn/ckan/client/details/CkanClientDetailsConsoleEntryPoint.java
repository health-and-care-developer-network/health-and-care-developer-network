package uk.nhs.hdn.ckan.client.details;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.details.DetailsAction.dataset_by_name;
import static uk.nhs.hdn.ckan.client.details.DetailsAction.values;

public final class CkanClientDetailsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DetailsOption = "details";
	private static final String KeyOption = "key";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientDetailsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DetailsOption, enumDescription(values())).withRequiredArg().ofType(DetailsAction.class).defaultsTo(dataset_by_name).describedAs("gets details");
		options.accepts(KeyOption, "name or UUID").withRequiredArg().ofType(String.class).describedAs("a name or UUID");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final DetailsAction detailsAction = defaulted(optionSet, DetailsOption);
		@NotNull final String key = required(optionSet, KeyOption);

		detailsAction.execute(DataGovUk, key);
	}
}
