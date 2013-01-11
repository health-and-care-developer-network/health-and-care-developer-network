package uk.nhs.hcdn.barcodes.gs1.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Authenticator;

public class ExampleRestEndpoint extends AbstractRestEndpoint
{
	protected ExampleRestEndpoint(@NotNull final String relativePath, @Nullable final Authenticator authenticator)
	{
		super(relativePath, authenticator);
	}
}
