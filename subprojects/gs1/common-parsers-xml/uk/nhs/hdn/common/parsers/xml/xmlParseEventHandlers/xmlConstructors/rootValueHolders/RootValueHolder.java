package uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;

public final class RootValueHolder<V>
{
	@Nullable
	private V value;

	public RootValueHolder()
	{
		value = null;
	}

	public void assign(@NotNull final V value)
	{
		if (this.value != null)
		{
			throw new IllegalStateException("Duplicate assignment");
		}
		this.value = value;
	}

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
