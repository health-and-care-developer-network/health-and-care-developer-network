package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;

public interface RootValueHolder<V, F>
{
	void assign(@NotNull final V value);

	@NotNull
	F retrieve() throws XmlSchemaViolationException;
}
