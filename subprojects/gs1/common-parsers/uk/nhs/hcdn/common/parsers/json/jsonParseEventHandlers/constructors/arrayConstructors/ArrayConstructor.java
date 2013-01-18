/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.Constructor;

import java.math.BigDecimal;

public interface ArrayConstructor<A> extends Constructor<A>
{
	@NotNull
	A newCollector();

	void addLiteralBooleanValue(@NotNull final A arrayCollector, final int index, final boolean value);

	void addLiteralNullValue(@NotNull final A arrayCollector, final int index);

	void addConstantStringValue(@NotNull final A arrayCollector, final int index, @NotNull final String value);

	void addConstantNumberValue(@NotNull final A arrayCollector, final int index, final long value);

	void addConstantNumberValue(@NotNull final A arrayCollector, final int index, @NotNull final BigDecimal value);

	void addObjectValue(@NotNull final A arrayCollector, final int index, @NotNull final Object value);

	void addArrayValue(@NotNull final A arrayCollector, final int index, @NotNull final Object value);

	@NotNull
	Object collect(@NotNull final A arrayCollector);
}
