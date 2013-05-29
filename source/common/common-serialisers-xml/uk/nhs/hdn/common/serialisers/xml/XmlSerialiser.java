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

package uk.nhs.hdn.common.serialisers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.tuples.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.tuples.Pair.pair;
import static uk.nhs.hdn.common.xml.XmlNamespaceUri.XmlSchemaInstanceNamespace;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "ClassWithTooManyMethods"})
public final class XmlSerialiser extends AbstractSerialiser
{
	@SafeVarargs
	public static void serialise(@NonNls @NotNull final String rootNodeName, @NotNull final Serialisable graph, @NotNull final OutputStream outputStream, @NotNull final Pair<String, String>... rootAttributes) throws CouldNotSerialiseException
	{
		serialise(true, rootNodeName, graph, outputStream, Utf8, of(pair(XmlSchemaInstanceNamespace, "xsi")), rootAttributes);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public static void serialise(final boolean xmlDeclaration, @NotNull final String rootNodeName, @NotNull final Serialisable graph, @NotNull final OutputStream outputStream, @NotNull final Charset charset, @NotNull final Pair<String, String>[] namespaceUriToPrefixes, @NotNull final Pair<String, String>[] rootAttributes) throws CouldNotSerialiseException
	{
		final XmlSerialiser xmlSerialiser = new XmlSerialiser(xmlDeclaration, rootNodeName, namespaceUriToPrefixes, rootAttributes);
		xmlSerialiser.serialise(graph, outputStream, charset);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private static final Pair<String, String>[] Empty = new Pair[0];

	@NonNls
	@NotNull
	private static final String XmlnsPrefixColon = "xmlns:";
	private static final int Space = (int) ' ';
	private static final int DoubleQuote = (int) '"';
	private static final char[] EqualsDoubleQuote = {'=', '"'};
	private static final int LessThan = (int) '<';
	private static final int GreaterThan = (int) '>';
	private static final char[] LessThanSlash = characters("</");
	private static final char[] SlashGreaterThan = characters("/>");
	private static final String ListElementNodeName = "element";

	private static char[] characters(final String value)
	{
		return value.toCharArray();
	}

	@NotNull
	private final String rootNodeName;
	private final boolean xmlDeclaration;
	@Nullable
	private final Pair<String, String> xsiNilAttribute;
	@NotNull
	private final Pair<String, String>[] rootAttributes;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull @ExcludeFromToString
	private XmlStringWriter xmlStringWriter;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public XmlSerialiser(final boolean xmlDeclaration, @NonNls @NotNull final String rootNodeName, @NotNull final Pair<String, String>... namespaceUriToPrefixes)
	{
		this(xmlDeclaration, rootNodeName, namespaceUriToPrefixes, Empty);
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public XmlSerialiser(final boolean xmlDeclaration, @NonNls @NotNull final String rootNodeName, @NotNull final Pair<String, String>[] namespaceUriToPrefixes, @NotNull final Pair<String, String>... rootAttributes)
	{
		this.rootNodeName = rootNodeName;
		this.xmlDeclaration = xmlDeclaration;
		@Nullable String xmlSchemaInstancePrefix = null;
		final Map<String, String> rootNodeAttributes = new HashMap<>(namespaceUriToPrefixes.length + rootAttributes.length);
		for (final Pair<String, String> namespaceUriToPrefix : namespaceUriToPrefixes)
		{
			final String uri = namespaceUriToPrefix.a;
			final String prefix = namespaceUriToPrefix.b;
			if (prefix.startsWith("xml"))
			{
				throw new IllegalArgumentException("namespace prefixes can not start with xml");
			}
			if (rootNodeAttributes.put(XmlnsPrefixColon + uri, prefix) != null)
			{
				throw new IllegalArgumentException("Duplicate namespace");
			}
			if (uri.equals(XmlSchemaInstanceNamespace))
			{
				xmlSchemaInstancePrefix = prefix;
			}
		}
		for (final Pair<String, String> rootAttribute : rootAttributes)
		{
			rootAttribute.putUniquelyInMap(rootNodeAttributes);
		}
		final int size = rootNodeAttributes.size();
		this.rootAttributes = new Pair[size];
		int index = 0;
		for (final Map.Entry<String, String> toPair : rootNodeAttributes.entrySet())
		{
			this.rootAttributes[index] = new Pair<>(toPair);
			index++;
		}
		xsiNilAttribute = xmlSchemaInstancePrefix == null ? null : pair(xmlSchemaInstancePrefix + ":nil", "true");
	}

	public void serialise(@NotNull final Serialisable graph, @NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotSerialiseException
	{
		try
		{
			start(outputStream, charset);
			graph.serialise(this);
			finish();
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotSerialiseException(graph, e);
		}
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		xmlStringWriter = new XmlStringWriter(writer);
		if (xmlDeclaration)
		{
			try
			{
				writer.write(format(ENGLISH, "<?xml version=\"1.0\" encoding=\"%1$s\" standalone=\"yes\"?>", charset.name().toUpperCase(ENGLISH)));
			}
			catch (IOException e)
			{
				throw new CouldNotWriteDataException(e);
			}
		}
		else if (!charset.equals(Utf8))
		{
			throw new IllegalStateException("If a XML declaration is omitted, then the charset must be UTF-8");
		}
		try
		{
			writeOpen(rootNodeName, false, rootAttributes);
		}
		catch (CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@Override
	public void finish() throws CouldNotWriteDataException
	{
		try
		{
			writeClose(rootNodeName, false);
		}
		catch (CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteDataException(e);
		}
		finally
		{
			super.finish();
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final String value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		if (value.isEmpty())
		{
			writeEmptyProperty(name, isMapEntry);
			return;
		}
		try
		{
			writeOpen(name, isMapEntry);
			writeText(value);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final MapSerialisable value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(value);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final ValueSerialisable value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(value);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public <S extends MapSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(values);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public <S extends ValueSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(values);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final List<?> values, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(values);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final Set<?> values, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(values);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final int value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(value);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final long value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name, isMapEntry);
			writeValue(value);
			writeClose(name, isMapEntry);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writePropertyNull(@NonNls @NotNull final String name, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		if (xsiNilAttribute == null)
		{
			writeEmptyProperty(name, isMapEntry);
		}
		else
		{
			writeEmptyProperty(name, isMapEntry, xsiNilAttribute);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		try
		{
			for (final S value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName, false);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		try
		{
			for (final S value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName, false);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@Override
	public void writeValue(@NotNull final List<?> values) throws CouldNotWriteValueException
	{
		try
		{
			for (final Object value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName, false);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@Override
	public void writeValue(@NotNull final Set<?> values) throws CouldNotWriteValueException
	{
		try
		{
			for (final Object value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName, false);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@SuppressWarnings("FinalMethodInFinalClass")
	@SafeVarargs
	@Override
	public final <S extends Serialisable> void writeValue(@NotNull final S... values) throws CouldNotWriteValueException
	{
		try
		{
			for (final Object value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName, false);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@Override
	public void writeValue(final int value) throws CouldNotWriteValueException
	{
		writeValue(Integer.toString(value));
	}

	@Override
	public void writeValue(final long value) throws CouldNotWriteValueException
	{
		writeValue(Long.toString(value));
	}

	@Override
	public void writeValue(@NotNull final BigDecimal value) throws CouldNotWriteValueException
	{
		writeValue(value.toString());
	}

	@Override
	public void writeValue(@NotNull final String value) throws CouldNotWriteValueException
	{
		try
		{
			writeText(value);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final MapSerialisable value) throws CouldNotWriteValueException
	{
		try
		{
			value.serialiseMap(this);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final ValueSerialisable value) throws CouldNotWriteValueException
	{
		try
		{
			value.serialiseValue(this);
		}
		catch (CouldNotSerialiseValueException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final UUID value) throws CouldNotWriteValueException
	{
		writeValue(value.toString());
	}

	@Override
	public void writeValueNull()
	{
	}

	@SuppressWarnings("FinalMethodInFinalClass") // final is required for @SafeVarargs
	@SafeVarargs
	private final void writeOpen(final CharSequence name, final boolean isMapEntry, final Pair<String, String>... attributes) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThan);
		writeNodeName(name, isMapEntry);
		writeAttributes(name, isMapEntry, attributes);
		write(GreaterThan);
	}

	private void writeClose(final CharSequence name, final boolean isMapEntry) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThanSlash);
		writeNodeName(name, isMapEntry);
		write(GreaterThan);
	}

	@SuppressWarnings("FinalMethodInFinalClass") // final is required for @SafeVarargs
	@SafeVarargs
	private final void writeEmpty(final CharSequence name, final boolean isMapEntry, final Pair<String, String>... attributes) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThan);
		writeNodeName(name, isMapEntry);
		writeAttributes(name, isMapEntry, attributes);
		write(SlashGreaterThan);
	}

	@SuppressWarnings({"FinalMethodInFinalClass", "FeatureEnvy"}) // final is required for @SafeVarargs
	@SafeVarargs
	private final void writeAttributes(final CharSequence name, final boolean isMapEntry, final Pair<String, String>... attributes) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		if (isMapEntry)
		{
			write(Space);
			xmlStringWriter.writeAttributeName("key");
			write(EqualsDoubleQuote);
			xmlStringWriter.writeAttributeValue(name);
			write(DoubleQuote);
		}

		for (final Pair<String, String> attribute : attributes)
		{
			write(Space);
			@NonNls final String attributeName = attribute.a;
			if (isMapEntry && "key".equals(attributeName))
			{
				throw new CouldNotEncodeDataException("An attribute 'key' is present, which is reserved by the serialiser for map entries (which this is)");
			}
			xmlStringWriter.writeAttributeName(attributeName);
			write(EqualsDoubleQuote);
			xmlStringWriter.writeAttributeValue(attribute.b);
			write(DoubleQuote);
		}
	}

	@SuppressWarnings("FinalMethodInFinalClass") // final is required for @SafeVarargs
	@SafeVarargs
	private final void writeEmptyProperty(final String name, final boolean isMapEntry, final Pair<String, String>... attributes) throws CouldNotWritePropertyException
	{
		try
		{
			writeEmpty(name, isMapEntry, attributes);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWritePropertyException(name, "", e);
		}
	}

	private void writeNodeName(final CharSequence name, final boolean isMapEntry) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		xmlStringWriter.writeNodeName(isMapEntry ? "map-entry" : name);
	}

	private void writeText(final CharSequence value) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		xmlStringWriter.writeText(value);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private void write(final char[] character) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(character);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	private void write(final int character) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(character);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}
}
