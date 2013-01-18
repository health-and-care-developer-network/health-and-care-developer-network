/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GenericRootArrayConstructor extends ListArrayConstructor
{
	@SuppressWarnings("RefusedBequest")
	@NotNull
	@Override
	public Object collect(@NotNull final List<Object> arrayCollector)
	{
		return arrayCollector.get(0);
	}
}
