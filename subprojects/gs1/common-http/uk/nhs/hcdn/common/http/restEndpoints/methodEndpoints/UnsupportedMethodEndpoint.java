package uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.Set;

public final class UnsupportedMethodEndpoint extends AbstractBodylessMethodEndpoint
{
	private static final char Comma = ',';

	@NotNull
	private final String allowHeaderValue;

	@NotNull
	private final GregorianCalendar lastModified;

	public UnsupportedMethodEndpoint(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Set<String> supportedMethodEndpoints, @NotNull final GregorianCalendar lastModified)
	{
		this.lastModified = (GregorianCalendar) lastModified.clone();
		final StringWriter stringWriter = new StringWriter(100);
		boolean first = true;
		for (final String supportedMethodEndpoint : supportedMethodEndpoints)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringWriter.append(Comma);
			}
			stringWriter.append(supportedMethodEndpoint);
		}
		allowHeaderValue = stringWriter.toString();
	}

	@SuppressWarnings("ReturnOfDateField")
	@NotNull
	@Override
	protected GregorianCalendar lastModified()
	{
		return lastModified;
	}

	@Override
	protected int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		responseHeaders.set(AllowHeaderName, allowHeaderValue);
		return ResponseCode.MethodNotAllowedResponseCode;
	}
}
