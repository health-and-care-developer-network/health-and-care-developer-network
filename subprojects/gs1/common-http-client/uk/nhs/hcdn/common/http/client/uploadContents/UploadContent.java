package uk.nhs.hcdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.client.connectionConfigurations.ConnectionConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface UploadContent extends ConnectionConfiguration
{
	void upload(@NotNull final HttpURLConnection httpConnection) throws IOException;
}
