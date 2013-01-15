package uk.nhs.hcdn.common.http.restEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Authenticator;

public class AbstractRegisterableMethodRoutingRestEndpoint extends AbstractMethodRoutingRestEndpoint
{
	protected AbstractRegisterableMethodRoutingRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		super(absoluteRawPath, authenticator);
	}
}
