package uk.nhs.hdn.common.http.client.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.JavaHttpClient;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;

import java.io.UnsupportedEncodingException;

import static java.net.URLEncoder.encode;
import static uk.nhs.hdn.common.http.UrlHelper.commonPortNumber;
import static uk.nhs.hdn.common.http.UrlHelper.toUrl;
import static uk.nhs.hdn.common.http.client.connectionConfigurations.ChunkedUploadsConnectionConfiguration.DoesNotSupportChunkedUploads;

public final class JsonGenericGetApi extends AbstractToString
{
	private static final char Slash = '/';
	private static final char Ampersand = '&';
	private static final char QuestionMark = '?';
	private static final char Equals = '=';

	private final boolean useHttps;
	@NotNull @NonNls private final String domainName;
	private final char portNumber;
	@NotNull @NonNls private final String absoluteUrlPath;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public JsonGenericGetApi(final boolean useHttps, @NonNls @NotNull final String domainName, @NonNls @NotNull final String absoluteUrlPath)
	{
		this(useHttps, domainName, commonPortNumber(useHttps), absoluteUrlPath);
	}

	public JsonGenericGetApi(final boolean useHttps, @NonNls @NotNull final String domainName, final char portNumber, @NotNull final String absoluteUrlPath)
	{
		this.useHttps = useHttps;
		this.domainName = domainName;
		this.portNumber = portNumber;
		this.absoluteUrlPath = absoluteUrlPath;
	}

	@NotNull
	public <V> ApiMethod<V> newApiMethod(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String... urlPieces)
	{
		return newApiUsingAbsoluteUrlPath(jsonSchema, urlPath(urlPieces));
	}

	@SuppressWarnings("FinalMethodInFinalClass") // due to @SafeArgs
	@SafeVarargs
	@NotNull
	public final <V> ApiMethod<V> newApiMethod(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String[] urlPieces, final Pair<String, String>... unencodedQueryStringParameters)
	{
		return newApiUsingAbsoluteUrlPath(jsonSchema, urlPath(urlPieces, unencodedQueryStringParameters));
	}

	@NotNull
	private <V> ApiMethod<V> newApiUsingAbsoluteUrlPath(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String absoluteUrlPath)
	{
		final HttpClient httpClient = new JavaHttpClient(toUrl(useHttps, domainName, portNumber, absoluteUrlPath), DoesNotSupportChunkedUploads);
		return new JsonApiMethod<>(httpClient, jsonSchema);
	}

	private String urlPath(final String... urlPieces)
	{
		final StringBuilder stringBuilder = new StringBuilder(absoluteUrlPath);
		for (final String urlPiece : urlPieces)
		{
			stringBuilder.append(Slash).append(urlPiece);
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings("FinalMethodInFinalClass") // @SafeVarargs causes this problem
	@SafeVarargs
	private final String urlPath(final String[] urlPieces, final Pair<String, String>... unencodedQueryStringParameters)
	{
		final String urlPath = urlPath(urlPieces);
		final StringBuilder stringBuilder = new StringBuilder(urlPath);
		boolean afterFirst = false;
		for (final Pair<String, String> unencodedQueryStringParameter : unencodedQueryStringParameters)
		{
			if (afterFirst)
			{
				stringBuilder.append(Ampersand);
			}
			else
			{
				stringBuilder.append(QuestionMark);
				afterFirst = true;
			}
			stringBuilder.append(encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(unencodedQueryStringParameter.a));
			stringBuilder.append(Equals);
			stringBuilder.append(encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(unencodedQueryStringParameter.b));
		}
		return stringBuilder.toString();
	}

	@NotNull
	private static String encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(@NotNull final String value)
	{
		try
		{
			return encode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ShouldNeverHappenException(e);
		}
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final JsonGenericGetApi that = (JsonGenericGetApi) obj;

		if ((int) portNumber != (int) that.portNumber)
		{
			return false;
		}
		if (useHttps != that.useHttps)
		{
			return false;
		}
		if (!absoluteUrlPath.equals(that.absoluteUrlPath))
		{
			return false;
		}
		if (!domainName.equals(that.domainName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = useHttps ? 1 : 0;
		result = 31 * result + domainName.hashCode();
		result = 31 * result + (int) portNumber;
		result = 31 * result + absoluteUrlPath.hashCode();
		return result;
	}
}
