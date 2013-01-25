package uk.nhs.hcdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface UploadContent
{
	void upload(@NotNull final HttpURLConnection httpConnection) throws IOException;
}
