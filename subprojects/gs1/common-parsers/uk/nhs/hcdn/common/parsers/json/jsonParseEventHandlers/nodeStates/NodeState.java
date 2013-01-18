/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;

import java.math.BigDecimal;

public interface NodeState<A>
{
	void key(@NotNull final String key);

	@NotNull
	ArrayConstructor<?> arrayValueStart();

	@NotNull
	ObjectConstructor<?> objectValueStart();

	void objectValue(@NotNull final Object value);

	void arrayValue(@NotNull final Object value);

	void literalBooleanValue(final boolean value);

	void literalNullValue();

	void constantStringValue(@NotNull final String value);

	void constantNumberValue(final long value);

	void constantNumberValue(@NotNull final BigDecimal value);

	@NotNull
	Object collect();
}
