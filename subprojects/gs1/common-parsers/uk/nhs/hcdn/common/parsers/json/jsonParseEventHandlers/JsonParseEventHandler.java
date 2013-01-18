/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface JsonParseEventHandler
{
	void startRoot();

	void endRoot();

	void startObject();

	void endObject();

	void startArray();

	void endArray();

	void literalNullValue();

	void literalBooleanValue(final boolean value);

	void numberValue(final long integerComponent);

	void numberValue(@NotNull final BigDecimal fractionComponent);

	void stringValue(@NotNull final String value);

	void key(@NotNull final String key);
}
