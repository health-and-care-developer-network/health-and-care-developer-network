package uk.nhs.hdn.dts.client.rats;

import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotUploadException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.rats.Message;
import uk.nhs.hdn.dts.rats.response.Response;

import java.util.HashMap;

public class Example2
{
	public void example() throws CorruptResponseException, CouldNotUploadException, UnacceptableResponseException, CouldNotConnectHttpException
	{
		final DtsRatsClient dtsRatsClient = new DtsRatsClient();

		final DtsName fromDtsName1 = new DtsName("SOME_NAME1");
		final LocalIdentifier localIdentifier1 = new LocalIdentifier("SOME_ID1");
		final Message message1 = new Message(fromDtsName1, localIdentifier1);

		final DtsName fromDtsName2 = new DtsName("SOME_NAME2");
		final LocalIdentifier localIdentifier2 = new LocalIdentifier("SOME_ID2");
		final Message message2 = new Message(fromDtsName2, localIdentifier2);

		final Response[] responses = dtsRatsClient.request(message1, message2);

		for (final Response response : responses)
		{
			if (response.isFor(message1))
			{
				// do something
			}
		}

		final HashMap<Message, Response> map = new HashMap<>(responses.length);
		for (final Response response : responses)
		{
			response.addToMap(map);
		}
		final Response responseForMessage1 = map.get(message1);
	}
}
