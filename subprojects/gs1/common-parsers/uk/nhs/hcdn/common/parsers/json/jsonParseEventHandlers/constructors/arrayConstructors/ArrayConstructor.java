/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.Constructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.math.BigDecimal;

public interface ArrayConstructor<A> extends Constructor<A>
{
	void addLiteralBooleanValue(@NotNull final A arrayCollector, final int index, final boolean value) throws SchemaViolationInvalidJsonException;

	void addLiteralNullValue(@NotNull final A arrayCollector, final int index) throws SchemaViolationInvalidJsonException;

	void addConstantStringValue(@NotNull final A arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException;

	void addConstantNumberValue(@NotNull final A arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException;

	void addConstantNumberValue(@NotNull final A arrayCollector, final int index, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException;

	void addObjectValue(@NotNull final A arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException;

	void addArrayValue(@NotNull final A arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException;

	@NotNull
	ArrayConstructor<?> arrayConstructor(final int index) throws SchemaViolationInvalidJsonException;

	@NotNull
	ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException;
}
