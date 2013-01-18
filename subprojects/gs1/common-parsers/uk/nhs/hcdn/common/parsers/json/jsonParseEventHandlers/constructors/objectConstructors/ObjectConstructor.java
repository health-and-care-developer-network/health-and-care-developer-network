/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.Constructor;

import java.math.BigDecimal;

public interface ObjectConstructor<A> extends Constructor<A>
{
	@NotNull
	A newCollector();

	void putLiteralBooleanValue(@NotNull final A objectCollector, @NotNull final String key, final boolean value);

	void putLiteralNullValue(@NotNull final A objectCollector, @NotNull final String key);

	void putConstantStringValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final String value);

	void putConstantNumberValue(@NotNull final A objectCollector, @NotNull final String key, final long value);

	void putConstantNumberValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final BigDecimal value);

	void putObjectValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final Object value);

	void putArrayValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final Object value);

	@NotNull
	Object collect(@NotNull final A objectCollector);
}
