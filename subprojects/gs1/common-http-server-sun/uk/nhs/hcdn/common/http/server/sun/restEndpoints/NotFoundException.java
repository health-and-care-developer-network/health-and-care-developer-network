/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

public final class NotFoundException extends AbstractRethrowableException
{
	public NotFoundException()
	{
		super("Not found");
	}
}
