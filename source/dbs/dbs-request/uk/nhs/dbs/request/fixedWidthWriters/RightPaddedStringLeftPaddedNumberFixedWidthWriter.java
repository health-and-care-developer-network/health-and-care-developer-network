package uk.nhs.dbs.request.fixedWidthWriters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;

import java.io.IOException;
import java.io.Writer;

public final class RightPaddedStringLeftPaddedNumberFixedWidthWriter implements FixedWidthWriter
{
	private static final char StringPaddingCharacter = ' ';
	private static final char NumberPaddingCharacter = '0';
	@NotNull
	private final Writer writer;
	@NotNull
	private final char[] newLineCharactersIfAny;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RightPaddedStringLeftPaddedNumberFixedWidthWriter(@NotNull final Writer writer, @NotNull final char... newLineCharactersIfAny)
	{
		this.writer = writer;
		this.newLineCharactersIfAny = newLineCharactersIfAny;
	}

	@Override
	public void writeString(final int width, @NotNull final CharSequence value) throws IOException, CouldNotEncodeDataException
	{
		final int length = value.length();

		if (length > width)
		{
			throw new CouldNotEncodeDataException("string overflows fixed width");
		}

		writer.append(value);

		if (length != width)
		{
			final int padding = width - length;
			writePadding(padding, StringPaddingCharacter);
		}
	}

	@Override
	public void writeUnsignedNumber(final int width, final int value) throws IOException, CouldNotEncodeDataException
	{
		writeUnsignedNumber(width, (long) value);
	}

	@Override
	public void writeUnsignedNumber(final int width, final long value) throws IOException, CouldNotEncodeDataException
	{
		if (value < 0L)
		{
			throw new CouldNotEncodeDataException("value is negative");
		}

		final String toWrite = Long.toString(value);
		final int length = toWrite.length();

		if (length > width)
		{
			throw new CouldNotEncodeDataException("unsigned number overflows fixed width");
		}

		if (length != width)
		{
			final int padding = width - length;
			writePadding(padding, NumberPaddingCharacter);
		}

		writer.append(toWrite);
	}

	@Override
	public void writeLine() throws IOException
	{
		writer.write(newLineCharactersIfAny);
	}

	private void writePadding(final int padding, final char paddingCharacter) throws IOException
	{
		for (int index = 0; index < padding; index++)
		{
			writer.append(paddingCharacter);
		}
	}
}
