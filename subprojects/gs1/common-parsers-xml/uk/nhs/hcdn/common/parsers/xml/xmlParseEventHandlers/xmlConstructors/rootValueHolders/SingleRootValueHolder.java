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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;

public final class SingleRootValueHolder<V> implements RootValueHolder<V, V>
{
	@Nullable
	private V value;

	public SingleRootValueHolder()
	{
		value = null;
	}

	@Override
	public void assign(@NotNull final V value)
	{
		if (this.value != null)
		{
			throw new IllegalStateException("Duplicate assignment");
		}
		this.value = value;
	}

	@Override
	@NotNull
	public V retrieve() throws XmlSchemaViolationException
	{
		if (value == null)
		{
			throw new XmlSchemaViolationException("No root node");
		}
		return value;
	}
}
