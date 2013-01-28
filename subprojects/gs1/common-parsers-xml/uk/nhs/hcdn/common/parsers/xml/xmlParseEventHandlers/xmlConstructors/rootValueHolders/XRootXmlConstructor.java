package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.Pair;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class XRootXmlConstructor<V> extends AbstractToString implements XmlConstructor<RootValueHolder<V>, V>
{
	@NotNull
	public static <V> XRootXmlConstructor<V> xml(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		return new XRootXmlConstructor<>(rootNodeName, shouldPreserveWhitespace, xmlConstructor);
	}

	@NonNls
	@NotNull
	private final String rootNodeName;
	private final boolean shouldPreserveWhitespace;
	@NotNull
	private final Class<?> type;
	@NotNull
	private final XmlConstructor<?, V> xmlConstructor;

	public XRootXmlConstructor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		this.shouldPreserveWhitespace = shouldPreserveWhitespace;
		this.rootNodeName = rootNodeName;
		type = xmlConstructor.type();
		this.xmlConstructor = xmlConstructor;
	}

	public boolean shouldPreserveWhitespace()
	{
		return shouldPreserveWhitespace;
	}

	@NotNull
	@Override
	public Class<?> type()
	{
		return type;
	}

	@Override
	public void collectText(@NotNull final RootValueHolder<V> collector, @NotNull final String text, final boolean shouldPreserveWhitespace)
	{
		throw new IllegalStateException("text should not occur before or after a root node");
	}

	@NotNull
	@Override
	public RootValueHolder<V> start()
	{
		return new RootValueHolder<>();
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil, @Nullable final String type) throws XmlSchemaViolationException
	{
		if (!name.equals(rootNodeName))
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "expected root node %1$s not %2$s", rootNodeName, name));
		}
		if (isNil)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "Root node %1$s should not be nil", rootNodeName));
		}
		return xmlConstructor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void collectNode(@NotNull final RootValueHolder<V> collector, @NotNull final String name, @NotNull final Object value)
	{
		collector.assign((V) value);
	}

	@NotNull
	@Override
	public V finish(@NotNull final RootValueHolder<V> collector) throws XmlSchemaViolationException
	{
		return collector.retrieve();
	}
}
