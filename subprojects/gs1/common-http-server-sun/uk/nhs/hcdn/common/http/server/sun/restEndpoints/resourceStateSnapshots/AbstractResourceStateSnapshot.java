/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.System.currentTimeMillis;
import static uk.nhs.hcdn.common.GregorianCalendarHelper.toRfc2822Form;

public abstract class AbstractResourceStateSnapshot extends AbstractToString implements ResourceStateSnapshot
{
	@MillisecondsSince1970
	@ExcludeFromToString
	private final long lastModifiedTime;

	@NotNull
	private final String lastModifiedInRfc2822Form;

	protected AbstractResourceStateSnapshot(@NotNull final GregorianCalendar lastModified)
	{
		final GregorianCalendar lastModified1 = (GregorianCalendar) lastModified.clone();
		lastModifiedTime = lastModified.getTime().getTime();
		lastModifiedInRfc2822Form = toRfc2822Form(lastModified1);
	}

	@NotNull
	@Override
	public final String lastModifiedInRfc2822Form()
	{
		return lastModifiedInRfc2822Form;
	}

	@Override
	public final boolean ifUnmodifiedSincePreconditionFailed(@NotNull final Date lastModified)
	{
		return lastModifiedTime > lastModified.getTime();
	}

	@Override
	public final boolean ifModifiedSinceNotModified(@NotNull final Date lastModified)
	{
		return !(lastModified.getTime() > currentTimeMillis() || lastModifiedTime > lastModified.getTime());
	}
}
