package uk.nhs.hdn.ckan.client.query;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.query.QueryAction.values;

public final class HdnCkanQueryConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnCkanQueryConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		oneOfEnumAsOptionWithRequiredArgument(options, values(), String.class);
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final QueryAction queryAction = enumOptionChosen(optionSet, values());
		@NotNull final String key = required(optionSet, convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, queryAction));

		queryAction.execute(DataGovUk, key);
	}
}
