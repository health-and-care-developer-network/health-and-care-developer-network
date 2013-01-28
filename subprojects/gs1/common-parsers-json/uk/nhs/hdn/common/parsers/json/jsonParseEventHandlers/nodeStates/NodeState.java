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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.nodeStates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.math.BigDecimal;

public interface NodeState
{
	void key(@NotNull final String key);

	@NotNull
	ArrayConstructor<?> arrayValueStart() throws SchemaViolationInvalidJsonException;

	@NotNull
	ObjectConstructor<?> objectValueStart() throws SchemaViolationInvalidJsonException;

	void objectValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException;

	void arrayValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException;

	void literalBooleanValue(final boolean value) throws SchemaViolationInvalidJsonException;

	void literalNullValue() throws SchemaViolationInvalidJsonException;

	void constantStringValue(@NotNull final String value) throws SchemaViolationInvalidJsonException;

	void constantNumberValue(final long value) throws SchemaViolationInvalidJsonException;

	void constantNumberValue(@NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException;

	@Nullable
	Object collect() throws SchemaViolationInvalidJsonException;
}
