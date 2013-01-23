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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class JavaObjectXmlConstructor<V> extends AbstractToString implements XmlConstructor<Object[], V>
{
	@SafeVarargs
	@NotNull
	public static <V> JavaObjectXmlConstructor<V> schemaFor(@NotNull final Class<V> type, @NotNull final Pair<String, MissingFieldXmlConstructor<?, ?>>... xmlConstructorsForFields)
	{
		return new JavaObjectXmlConstructor<>(type, xmlConstructorsForFields);
	}

	@NotNull
	private final Class<V> type;
	@NotNull
	private final Map<String, XmlConstructor<?, ?>> xmlConstructorsForFields;
	private final MissingFieldXmlConstructor<?, ?>[] missingFieldHandlers;
	private final Map<String, Integer> constructorParameterIndices;
	@NotNull
	private final Constructor<V> constructor;
	private final int size;

	@SafeVarargs
	public JavaObjectXmlConstructor(@NotNull final Class<V> type, @NotNull final Pair<String, MissingFieldXmlConstructor<?, ?>>... xmlConstructorsForFields)
	{
		this.type = type;
		size = xmlConstructorsForFields.length;
		constructorParameterIndices = new HashMap<>(size);
		final Class<?>[] constructorParameterTypes = new Class[size];
		this.xmlConstructorsForFields = new HashMap<>(size);
		missingFieldHandlers = new MissingFieldXmlConstructor<?, ?>[size];
		for(int index = 0; index < size; index++)
		{
			final Pair<String, MissingFieldXmlConstructor<?, ?>> pair =  xmlConstructorsForFields[index];
			final String key = pair.a;
			final XmlConstructor<?, ?> xmlConstructor = pair.b;
			this.xmlConstructorsForFields.put(key, xmlConstructor);
			if (constructorParameterIndices.put(key, index) != null)
			{
				throw new IllegalArgumentException("Duplicate keys");
			}
			constructorParameterTypes[index] = xmlConstructor.type();
		}
		try
		{
			constructor = type.getDeclaredConstructor(constructorParameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalArgumentException("type %1$s has no constructor for provided xmlConstructors", e);
		}
	}

	@NotNull
	@Override
	public Class<V> type()
	{
		return type;
	}

	@Override
	@NotNull
	public Object[] start()
	{
		return new Object[size];
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> node(@NotNull final String name) throws XmlSchemaViolationException
	{
		@Nullable final XmlConstructor<?, ?> xmlConstructor = xmlConstructorsForFields.get(name);
		if (xmlConstructor == null)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "no known field for name %1$s", name));
		}
		return xmlConstructor;
	}

	@Override
	public void attribute(@NotNull final Object[] collector, @NotNull final String key, @NotNull final String value) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "attribute key %1$s, value %2$s was unexpected", key, value));
	}

	@Override
	public void text(@NotNull final Object[] collector, @NotNull final String text) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "text value %1$s was unexpected", text));
	}

	@Override
	public void node(@NotNull final Object[] collector, @NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		@Nullable final Integer integer = constructorParameterIndices.get(name);
		if (integer == null)
		{
			throw new IllegalStateException("should have an integer");
		}
		if (collector[integer] != null)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "duplicate field with name %1$s", name));
		}
		collector[integer] = value;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public V finish(@NotNull final Object[] collector) throws XmlSchemaViolationException
	{
		for(int index = 0; index < size; index++)
		{
			if (collector[index] == null)
			{
				collector[index] = missingFieldHandlers[index].missingFieldValue();
			}
		}

		try
		{
			return constructor.newInstance(collector);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new XmlSchemaViolationException(e);
		}
		catch (InvocationTargetException e)
		{
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new XmlSchemaViolationException(e.getTargetException());
		}
	}
}
