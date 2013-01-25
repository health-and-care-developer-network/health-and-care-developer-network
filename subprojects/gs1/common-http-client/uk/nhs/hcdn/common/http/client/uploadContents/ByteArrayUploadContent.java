package uk.nhs.hcdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.ContentTypeWithCharacterSet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ByteArrayUploadContent extends AbstractUploadContent
{
	@NotNull
	private final byte[] content;
	private final int length;

	@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter", "MethodCanBeVariableArityMethod"})
	public ByteArrayUploadContent(@ContentTypeWithCharacterSet @NonNls @NotNull final String contentType, @NotNull final byte[] content)
	{
		super(contentType);
		this.content = content;
		length = content.length;
	}

	@Override
	public final void upload(@NotNull final HttpURLConnection httpConnection) throws IOException
	{
		super.upload(httpConnection);
		httpConnection.setFixedLengthStreamingMode(length);
		final OutputStream outputStream = httpConnection.getOutputStream();
		boolean firstExceptionThrown = false;
		try
		{
			outputStream.write(content);
			firstExceptionThrown = true;
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				if (firstExceptionThrown)
				{
					throw e;
				}
			}
		}
	}
}
