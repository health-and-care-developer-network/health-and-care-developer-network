/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders;

import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

public final class InvalidCodePointException extends AbstractRethrowableException
{
	public InvalidCodePointException()
	{
		super("Invalid code point");
	}
}
