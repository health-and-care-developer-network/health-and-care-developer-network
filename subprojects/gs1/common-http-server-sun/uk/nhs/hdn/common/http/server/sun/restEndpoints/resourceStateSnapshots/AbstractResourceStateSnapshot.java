/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.System.currentTimeMillis;
import static uk.nhs.hdn.common.GregorianCalendarHelper.toRfc2822Form;

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
