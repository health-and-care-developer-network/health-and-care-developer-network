package uk.nhs.hdn.dts.client.rats;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.JavaHttpClient;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotUploadException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.xml.LegacyXmlByteArrayUploadContent;
import uk.nhs.hdn.common.http.client.xml.LegacyXmlGetHttpResponseUser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.rats.Message;
import uk.nhs.hdn.dts.rats.request.Messages;
import uk.nhs.hdn.dts.rats.response.Response;

import java.net.URL;

import static uk.nhs.hdn.common.http.UrlHelper.toUrl;
import static uk.nhs.hdn.common.http.client.connectionConfigurations.ChunkedUploadsConnectionConfiguration.DoesNotSupportChunkedUploads;
import static uk.nhs.hdn.dts.rats.request.Messages.messagesXmlSerialiser;
import static uk.nhs.hdn.dts.rats.response.schema.ResponsesSchemaParser.ResponsesSchemaParserInstance;

public final class DtsRatsClient extends AbstractToString
{
	@NotNull @NonNls public static final String DefaultDomainName = "nww.reg.nhs.uk";
	public static final char DefaultPort = (char) 80;
	@NotNull @NonNls public static final String DefaultPath = "/dts/message_tracking_api.asp";

	@NotNull private final HttpClient httpClient;

	public DtsRatsClient()
	{
		this(false, DefaultDomainName, DefaultPort, DefaultPath);
	}

	public DtsRatsClient(final boolean useHttps, @NotNull @NonNls final String domainName, final char portNumber, @NotNull @NonNls final String path)
	{
		this(toUrl(useHttps, domainName, portNumber, path));
	}

	private DtsRatsClient(@NotNull final URL url)
	{
		httpClient = new JavaHttpClient(url, DoesNotSupportChunkedUploads);
	}

	@NotNull
	public Response[] request(@NotNull final DtsName fromDtsName, @NotNull final LocalIdentifier localIdentifier) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException, CouldNotUploadException
	{
		return request(new Message(fromDtsName, localIdentifier));
	}

	@NotNull
	public Response[] request(@NotNull final Message... message) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException, CouldNotUploadException
	{
		final Messages messages = new Messages(message);
		return httpClient.post(new LegacyXmlByteArrayUploadContent(messages, messagesXmlSerialiser()), new LegacyXmlGetHttpResponseUser<>(ResponsesSchemaParserInstance));
	}
}
