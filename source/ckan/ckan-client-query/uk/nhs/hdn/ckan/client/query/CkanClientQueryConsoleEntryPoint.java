package uk.nhs.hdn.ckan.client.query;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.query.QueryAction.revisions_by_id;
import static uk.nhs.hdn.ckan.client.query.QueryAction.values;

public final class CkanClientQueryConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String QueryOption = "query";
	private static final String KeyOption = "key";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientQueryConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(QueryOption, enumDescription(values())).withRequiredArg().ofType(QueryAction.class).defaultsTo(revisions_by_id).describedAs("query details");
		options.accepts(KeyOption, "name, UUID, tag").withRequiredArg().ofType(String.class).describedAs("a name, UUID or tag");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final QueryAction queryAction = defaulted(optionSet, QueryOption);
		@NotNull final String key = required(optionSet, KeyOption);

		queryAction.execute(DataGovUk, key);
	}
}
