package uk.nhs.hcdn.common.http.restEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Authenticator;

public class AbstractMethodRoutingRestEndpoint extends AbstractRestEndpoint
{
	protected AbstractMethodRoutingRestEndpoint(@NonNls @NotNull final String relativePath, @Nullable final Authenticator authenticator)
	{
		super(relativePath, authenticator);
	}
}
