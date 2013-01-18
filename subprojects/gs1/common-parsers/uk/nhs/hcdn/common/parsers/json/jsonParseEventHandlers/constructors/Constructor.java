/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;

public interface Constructor<A>
{
	@NotNull
	ArrayConstructor<?> arrayConstructor();

	@NotNull
	ObjectConstructor<?> objectConstructor();
}
