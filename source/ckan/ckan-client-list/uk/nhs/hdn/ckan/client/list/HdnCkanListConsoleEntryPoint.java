package uk.nhs.hdn.ckan.client.list;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.commandLine.ShouldHaveExitedException;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.list.ListAction.values;

public final class HdnCkanListConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnCkanListConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		oneOfEnumAsOption(options, values());
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final ListAction listAction = enumOptionChosen(optionSet, values());

		if (optionSet.hasArgument(listAction.name()))
		{
			exitWithErrorAndHelp("--%1$s does not take an argument", convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, listAction));
			throw new ShouldHaveExitedException();
		}

		listAction.execute(DataGovUk);
	}
}
