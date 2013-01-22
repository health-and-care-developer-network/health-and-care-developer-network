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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlCollectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class JavaObjectXmlConstructor<V> extends AbstractToString implements XmlConstructor<Object[], V>
{
	@NotNull
	private final Class<V> type;
	@NotNull
	private final Map<String, XmlConstructor<?, ?>> xmlConstructorsForFields;
	private final Map<String, Integer> constructorParameterIndices;
	@NotNull
	private final Constructor<V> constructor;
	private final int size;

	public JavaObjectXmlConstructor(@NotNull final Class<V> type, @NotNull final Constructor<V> x, @SuppressWarnings("CollectionDeclaredAsConcreteClass") @NotNull final LinkedHashMap<String, XmlConstructor<?, ?>> xmlConstructorsForFields)
	{
		this.type = type;
		final Collection<XmlConstructor<?,?>> values = xmlConstructorsForFields.values();
		size = values.size();
		this.xmlConstructorsForFields = new HashMap<>(xmlConstructorsForFields);
		constructorParameterIndices = new HashMap<>(size);
		final Class<?>[] constructorParameterTypes = new Class[size];
		int index = 0;
		for (final Map.Entry<String, XmlConstructor<?, ?>> stringXmlConstructorEntry : xmlConstructorsForFields.entrySet())
		{
			constructorParameterIndices.put(stringXmlConstructorEntry.getKey(), index);
			constructorParameterTypes[index] = stringXmlConstructorEntry.getValue().type();
			index++;
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
