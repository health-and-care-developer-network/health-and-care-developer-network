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

package uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.GregorianCalendarHelper;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static uk.nhs.hdn.common.GregorianCalendarHelper.toRfc2822Form;

public abstract class AbstractSubResource extends AbstractToString implements SubResource
{
	private final long lastModifiedTime;
	@NotNull @NonNls private final String lastModifiedInRfc2822Form;

	protected AbstractSubResource(@MillisecondsSince1970 final long lastModifiedTime)
	{
		this.lastModifiedTime = lastModifiedTime;
		lastModifiedInRfc2822Form = toRfc2822Form(GregorianCalendarHelper.utc(lastModifiedTime));
	}

	@NonNls
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
