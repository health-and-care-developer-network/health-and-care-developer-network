package uk.nhs.hdn.ckan.client.revisions;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.client.revisions.RevisionsAction.revisions_by_id;
import static uk.nhs.hdn.ckan.client.revisions.RevisionsAction.values;

public final class CkanClientRevisionsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String RevisionsOption = "revisions";
	private static final String KeyOption = "key";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(CkanClientRevisionsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(RevisionsOption, enumDescription(values())).withRequiredArg().ofType(RevisionsAction.class).defaultsTo(revisions_by_id).describedAs("revisions details");
		options.accepts(KeyOption, "name, UUID, tag").withRequiredArg().ofType(String.class).describedAs("a name, UUID or tag");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final RevisionsAction revisionsAction = defaulted(optionSet, RevisionsOption);
		@NotNull final String key = required(optionSet, KeyOption);

		revisionsAction.execute(DataGovUk, key);
	}
}
