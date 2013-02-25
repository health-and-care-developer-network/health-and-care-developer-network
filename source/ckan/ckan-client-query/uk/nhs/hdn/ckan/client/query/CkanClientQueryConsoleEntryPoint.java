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

public final class CkanClientQueryConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientQueryConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.acceptsAll(enumAsLongOptions(values()), enumDescription(values(), false, true)).withRequiredArg().ofType(String.class).describedAs("a name, UUID, tag or timestamp (eg 2013-01-28T20:06:30.061645) as appropriate");
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
