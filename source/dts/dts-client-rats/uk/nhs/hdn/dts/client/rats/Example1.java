package uk.nhs.hdn.dts.client.rats;

import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotUploadException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.rats.response.Response;

import static uk.nhs.hdn.dts.rats.response.ResponseStatus.NoMatch;

public class Example1
{
	public void example() throws CorruptResponseException, CouldNotUploadException, UnacceptableResponseException, CouldNotConnectHttpException
	{
		final DtsName fromDtsName = new DtsName("SOME_NAME");
		final LocalIdentifier localIdentifier = new LocalIdentifier("SOME_ID");
		final DtsRatsClient dtsRatsClient = new DtsRatsClient();
		final Response response = dtsRatsClient.request(fromDtsName, localIdentifier);
		final boolean b = response.hasStatus(NoMatch);
	}
}
