/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.exceptions;

public final class ImpossibleEnumeratedStateException extends IllegalStateException
{
	public ImpossibleEnumeratedStateException()
	{
		super("Impossible enumerated state");
	}
}
