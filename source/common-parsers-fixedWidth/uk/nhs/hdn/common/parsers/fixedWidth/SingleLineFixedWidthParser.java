package uk.nhs.hdn.common.parsers.fixedWidth;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.CouldNotConvertFieldValueException;
import uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthLineUsers.FixedWidthLineUser;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import java.io.BufferedReader;
import java.io.IOException;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public class SingleLineFixedWidthParser<V> extends AbstractToString implements Parser
{
	@NotNull
	private final FixedWidthLineUser<V> fixedWidthLineUser;
	@NotNull
	private final ParseResultUser<V> parseResultUser;
	@NotNull
	private final FieldSchema[] fieldSchemas;
	@ExcludeFromToString
	private final int length;

	public SingleLineFixedWidthParser(@NotNull final FixedWidthLineUser<V> fixedWidthLineUser, @NotNull final ParseResultUser<V> parseResultUser, @NotNull final FieldSchema... fieldSchemas)
	{
		this.fixedWidthLineUser = fixedWidthLineUser;
		this.parseResultUser = parseResultUser;
		this.fieldSchemas = copyOf(fieldSchemas);
		length = fieldSchemas.length;
	}

	@Override
	public final void parse(@NotNull final BufferedReader bufferedReader, @MillisecondsSince1970 final long lastModified) throws IOException, CouldNotParseException
	{
		final Object[] collectedFields = new Object[length];
		for (int index = 0; index < length; index++)
		{
			final FieldSchema field = fieldSchemas[index];
			try
			{
				collectedFields[index] = field.parseAndConvert(bufferedReader);
			}
			catch (CouldNotConvertFieldValueException e)
			{
				throw new CouldNotParseException(0, e);
			}
		}

		final V result;
		try
		{
			result = fixedWidthLineUser.use(0, collectedFields);
		}
		catch (CouldNotConvertFieldsException e)
		{
			throw new CouldNotParseException(0, e);
		}
		parseResultUser.use(result);
	}
}
