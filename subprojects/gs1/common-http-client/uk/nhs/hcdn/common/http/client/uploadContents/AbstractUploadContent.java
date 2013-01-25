package uk.nhs.hcdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.ContentTypeWithCharacterSet;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.net.HttpURLConnection;

import static uk.nhs.hcdn.common.http.RequestHeader.ContentTypeHeaderName;

public abstract class AbstractUploadContent extends AbstractToString implements UploadContent
{
	@ContentTypeWithCharacterSet
	@NonNls
	@NotNull
	private final String contentType;

	@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter", "MethodCanBeVariableArityMethod"})
	protected AbstractUploadContent(@ContentTypeWithCharacterSet @NonNls @NotNull final String contentType)
	{
		this.contentType = contentType;
	}

	@Override
	public void upload(@NotNull final HttpURLConnection httpConnection) throws IOException
	{
		httpConnection.setDoOutput(true);
		httpConnection.setRequestProperty(ContentTypeHeaderName, contentType);
	}
}
