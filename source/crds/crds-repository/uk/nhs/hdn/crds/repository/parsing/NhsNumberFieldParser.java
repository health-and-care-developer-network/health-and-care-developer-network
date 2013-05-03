package uk.nhs.hdn.crds.repository.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.AbstractMandatoryPrefixFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.FieldParser;
import uk.nhs.hdn.number.NhsNumber;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;

public final class NhsNumberFieldParser extends AbstractMandatoryPrefixFieldParser<NhsNumber>
{
	@NotNull public static final FieldParser<NhsNumber> NhsNumberParserInstance = new NhsNumberFieldParser();

	private NhsNumberFieldParser()
	{
	}

	@NotNull
	@Override
	public NhsNumber parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		final String field = NonEmptyStringFieldParserInstance.parse(fieldIndex, fieldValue);
		try
		{
			return NhsNumber.valueOf(field);
		}
		catch (IllegalArgumentException e)
		{
			throw new CouldNotParseFieldException(fieldIndex, field, e);
		}
	}
}
