package uk.nhs.hdn.common.http.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

public interface ApiMethod<V>
{
	@NotNull
	V execute() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException;
}
