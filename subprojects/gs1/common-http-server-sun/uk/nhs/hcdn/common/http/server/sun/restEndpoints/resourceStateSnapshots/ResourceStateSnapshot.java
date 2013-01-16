/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.BadRequestException;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.NotFoundException;

import java.util.Date;

public interface ResourceStateSnapshot
{
	@NotNull @NonNls
	String lastModifiedInRfc2822Form();

	boolean ifModifiedSinceNotModified(@NotNull final Date lastModified);

	boolean ifUnmodifiedSincePreconditionFailed(@NotNull final Date lastModified);

	@NotNull
	ResourceContent content(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString) throws NotFoundException, BadRequestException;
}
