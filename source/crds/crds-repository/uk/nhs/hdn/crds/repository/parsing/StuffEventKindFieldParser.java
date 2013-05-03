package uk.nhs.hdn.crds.repository.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.AbstractMandatoryPrefixFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.FieldParser;
import uk.nhs.hdn.crds.registry.domain.StuffEventKind;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;

public final class StuffEventKindFieldParser extends AbstractMandatoryPrefixFieldParser<StuffEventKind>
{
	@NotNull public static final FieldParser<StuffEventKind> StuffEventKindParserInstance = new StuffEventKindFieldParser();

	private StuffEventKindFieldParser()
	{
	}

	@NotNull
	@Override
	public StuffEventKind parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		final String field = NonEmptyStringFieldParserInstance.parse(fieldIndex, fieldValue);
		try
		{
			return StuffEventKind.valueOf(field);
		}
		catch (IllegalArgumentException e)
		{
			throw new CouldNotParseFieldException(fieldIndex, field, e);
		}
	}
}
