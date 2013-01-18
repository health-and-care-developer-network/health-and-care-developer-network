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
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListArrayConstructor extends AbstractToString implements ArrayConstructor<List<Object>>
{
	@Nullable
	private ObjectConstructor<?> objectConstructor;

	public ListArrayConstructor()
	{
		objectConstructor = null;
	}

	public void configure(@NotNull final ObjectConstructor<?> objectConstructor)
	{
		this.objectConstructor = objectConstructor;
	}

	@NotNull
	@Override
	public List<Object> newCollector()
	{
		return new ArrayList<>(100);
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor()
	{
		return this;
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor()
	{
		assert objectConstructor != null;
		return objectConstructor;
	}

	@Override
	public void addLiteralBooleanValue(@NotNull final List<Object> arrayCollector, final int index, final boolean value)
	{
		arrayCollector.add(value);
	}

	@Override
	public void addLiteralNullValue(@NotNull final List<Object> arrayCollector, final int index)
	{
		arrayCollector.add(null);
	}

	@Override
	public void addConstantStringValue(@NotNull final List<Object> arrayCollector, final int index, @NotNull final String value)
	{
		arrayCollector.add(value);
	}

	@Override
	public void addConstantNumberValue(@NotNull final List<Object> arrayCollector, final int index, final long value)
	{
		arrayCollector.add(value);
	}

	@Override
	public void addConstantNumberValue(@NotNull final List<Object> arrayCollector, final int index, @NotNull final BigDecimal value)
	{
		arrayCollector.add(value);
	}

	@Override
	public void addObjectValue(@NotNull final List<Object> arrayCollector, final int index, @NotNull final Object value)
	{
		arrayCollector.add(value);
	}

	@Override
	public void addArrayValue(@NotNull final List<Object> arrayCollector, final int index, @NotNull final Object value)
	{
		arrayCollector.add(value);
	}

	@NotNull
	@Override
	public Object collect(@NotNull final List<Object> arrayCollector)
	{
		return arrayCollector;
	}
}
