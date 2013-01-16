/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;

import static uk.nhs.hcdn.common.GregorianCalendarHelper.utcNow;

public final class NotFoundResourceStateSnapshot extends AbstractResourceStateSnapshot
{
	public NotFoundResourceStateSnapshot()
	{
		super(utcNow());
	}

	@NotNull
	@Override
	public ResourceContent content(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString)
	{
		throw new UnsupportedOperationException("Should never be called");
	}
}
