/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.jsonParseEventHandlers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface JsonParseEventHandler
{
	void nullValue();

	void booleanValue(final boolean value);

	void numberValue(final long integerComponent);

	void numberValue(final long integerComponent, final BigDecimal fractionComponent, final long exponentComponent);

	void stringValue(@NotNull final String value);

	void key(@NotNull final String key);
}
