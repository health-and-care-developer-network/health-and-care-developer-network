package uk.nhs.hdn.ckan.client.details;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.details.DetailsAction.values;

public final class HdnCkanDetailsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnCkanDetailsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		oneOfEnumAsOptionWithRequiredArgument(options, values(), String.class);
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException
	{
		@NotNull final DetailsAction detailsAction = enumOptionChosen(optionSet, values());
		@NotNull final String key = required(optionSet, convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, detailsAction));

		detailsAction.execute(DataGovUk, key);
	}
}
