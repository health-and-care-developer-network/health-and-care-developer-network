package uk.nhs.hdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.ContentTypeWithCharacterSet;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.net.HttpURLConnection;

import static uk.nhs.hdn.common.http.RequestHeader.ContentTypeHeaderName;

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
	public void configure(@NotNull final HttpURLConnection httpConnection)
	{
		httpConnection.setRequestProperty(ContentTypeHeaderName, contentType);
	}
}
