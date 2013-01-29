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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.NotFoundException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.SubResourceFinder;

import java.util.GregorianCalendar;

public abstract class AbstractWithSubResourcesResourceStateSnapshot extends AbstractResourceStateSnapshot implements SubResourceFinder
{
	protected AbstractWithSubResourcesResourceStateSnapshot(@NotNull final GregorianCalendar lastModified)
	{
		super(lastModified);
	}

	@NotNull
	@Override
	public final ResourceContent content(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString) throws NotFoundException, BadRequestException
	{
		return find(rawRelativeUriPath).content(rawQueryString);
	}
}
