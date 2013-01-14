/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString;

public abstract class AbstractToString
{
	protected AbstractToString()
	{
	}

	@Override
	public final String toString()
	{
		return ToStringHelper.toString(this);
	}
}
