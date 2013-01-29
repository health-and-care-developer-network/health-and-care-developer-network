package uk.nhs.hdn.common.http.client.uploadContents;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.connectionConfigurations.ConnectionConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface UploadContent extends ConnectionConfiguration
{
	void upload(@NotNull final HttpURLConnection httpConnection) throws IOException;
}
