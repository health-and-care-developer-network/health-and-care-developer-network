package uk.nhs.hcdn.common.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class XmlNamespaceUriHelper
{
	private static final char Colon = ':';

	private XmlNamespaceUriHelper()
	{
	}

	@NotNull
	public static String namespaceQualifiedNodeOrAttributeName(@XmlNamespaceUri @NonNls @NotNull final String uri, @NonNls @NotNull final String localName)
	{
		if (uri.isEmpty())
		{
			return localName;
		}
		return uri + Colon + localName;
	}
}
