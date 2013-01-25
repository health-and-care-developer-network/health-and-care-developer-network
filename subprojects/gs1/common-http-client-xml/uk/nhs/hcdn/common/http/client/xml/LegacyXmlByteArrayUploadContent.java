package uk.nhs.hcdn.common.http.client.xml;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.http.client.uploadContents.ByteArrayUploadContent;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hcdn.common.serialisers.Serialisable;
import uk.nhs.hcdn.common.serialisers.xml.XmlSerialiser;

import java.io.ByteArrayOutputStream;

import static uk.nhs.hcdn.common.CharsetHelper.Utf8;
import static uk.nhs.hcdn.common.http.ContentTypeWithCharacterSet.LegacyXmlContentTypeUtf8;

public class LegacyXmlByteArrayUploadContent extends ByteArrayUploadContent
{
	private static final int Guess = 4096;

	public LegacyXmlByteArrayUploadContent(@NotNull final Serialisable value, @NotNull final XmlSerialiser utf8XmlSerialiser)
	{
		super(LegacyXmlContentTypeUtf8, content(value, utf8XmlSerialiser));
	}

	@NotNull
	private static byte[] content(@NotNull final Serialisable value, @NotNull final XmlSerialiser utf8XmlSerialiser)
	{
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Guess);
		try
		{
			utf8XmlSerialiser.serialise(value, outputStream, Utf8);
		}
		catch (CouldNotSerialiseException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		return outputStream.toByteArray();
	}
}
