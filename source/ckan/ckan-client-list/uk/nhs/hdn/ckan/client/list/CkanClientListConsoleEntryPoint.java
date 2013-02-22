package uk.nhs.hdn.ckan.client.list;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.list.ListAction.dataset_names;
import static uk.nhs.hdn.ckan.client.list.ListAction.values;

public final class CkanClientListConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String ListOption = "list";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientListConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(ListOption, enumDescription(values())).withRequiredArg().ofType(ListAction.class).defaultsTo(dataset_names).describedAs("lists all record identifiers");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final ListAction listAction = defaulted(optionSet, ListOption);

		listAction.execute(DataGovUk);
	}
}
