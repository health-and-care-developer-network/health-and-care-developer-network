package uk.nhs.hdn.common.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.xml.XmlNamespaceUri.XmlNamespace;
import static uk.nhs.hdn.common.xml.XmlNamespaceUri.XmlSchemaInstanceNamespace;
import static uk.nhs.hdn.common.xml.XmlNamespaceUriHelper.namespaceQualifiedNodeOrAttributeName;

public final class XmlAttributesHelper
{
	@NonNls
	@NotNull
	public static final String XmlSpaceAttribute = namespaceQualifiedNodeOrAttributeName(XmlNamespace, "space");

	@NonNls
	@NotNull
	public static final String XmlSchemaNilAttribute = namespaceQualifiedNodeOrAttributeName(XmlSchemaInstanceNamespace, "nil");

	@NonNls
	@NotNull
	public static final String XmlSchemaTypeAttribute = namespaceQualifiedNodeOrAttributeName(XmlSchemaInstanceNamespace, "type");

	@NonNls
	@NotNull
	public static final String XmlSchemaSchemaLocationAttribute = namespaceQualifiedNodeOrAttributeName(XmlSchemaInstanceNamespace, "schemaLocation");

	@NonNls
	@NotNull
	public static final String XmlSchemaNoNamespaceSchemaLocationAttribute = namespaceQualifiedNodeOrAttributeName(XmlSchemaInstanceNamespace, "noNamespaceSchemaLocation");

	@SuppressWarnings({"HardCodedStringLiteral", "DuplicateStringLiteralInspection"})
	public static boolean isNil(@NotNull @NonNls final String attributeValue) throws XmlSchemaViolationException
	{
		switch(attributeValue)
		{
			case "true":
				return true;

			case "false":
				return false;

			default:
				throw new XmlSchemaViolationException(format(ENGLISH, "Unknown value '%1$s' for xsi:nil", attributeValue));
		}
	}

	@SuppressWarnings({"HardCodedStringLiteral", "DuplicateStringLiteralInspection"})
	public static boolean shouldPreserveWhitespace(final String attributeValue, final boolean parentShouldPreserveWhitespace) throws XmlSchemaViolationException
	{
		switch (attributeValue)
		{
			case "preserve":
				return true;

			case "default":
				return parentShouldPreserveWhitespace;

			default:
				throw new XmlSchemaViolationException(format(ENGLISH, "Unknown value '%1$s' for xml:space", attributeValue));
		}
	}

	private XmlAttributesHelper()
	{
	}
}
