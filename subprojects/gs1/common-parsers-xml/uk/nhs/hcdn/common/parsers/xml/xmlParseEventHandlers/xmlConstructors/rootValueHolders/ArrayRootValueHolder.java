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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;

import java.util.ArrayList;
import java.util.List;

public final class ArrayRootValueHolder<V> implements RootValueHolder<V, V[]>
{
	private static final int Guess = 100;
	@NotNull
	private final ArrayCreator<V> arrayCreator;
	@NotNull
	private final List<V> values;

	public ArrayRootValueHolder(@NotNull final ArrayCreator<V> arrayCreator)
	{
		this.arrayCreator = arrayCreator;
		values = new ArrayList<>(Guess);
	}

	@Override
	public void assign(@NotNull final V value)
	{
		values.add(value);
	}

	@NotNull
	@Override
	public V[] retrieve() throws XmlSchemaViolationException
	{
		return values.toArray(arrayCreator.newInstance1(values.size()));
	}
}
