/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.ckan;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.*;
import uk.nhs.hdn.ckan.domain.strings.DatasetNameString;
import uk.nhs.hdn.ckan.domain.strings.GroupNameString;
import uk.nhs.hdn.ckan.domain.strings.TagString;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.JavaHttpClient;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hdn.ckan.parsing.DatasetIdsArrayJsonSchema.DatasetIdsUuidSchemaInstance;
import static uk.nhs.hdn.ckan.parsing.DatasetNamesArrayJsonSchema.DatasetNamesSchemaInstance;
import static uk.nhs.hdn.ckan.parsing.GroupIdsArrayJsonSchema.GroupIdsUuidSchemaInstance;
import static uk.nhs.hdn.ckan.parsing.GroupNamesArrayJsonSchema.GroupNamesSchemaInstance;
import static uk.nhs.hdn.ckan.parsing.TagsArrayJsonSchema.TagsSchemaInstance;
import static uk.nhs.hdn.ckan.parsing.LicencesArrayJsonSchema.LicencesSchemaInstance;
import static uk.nhs.hdn.common.http.UrlHelper.commonPortNumber;
import static uk.nhs.hdn.common.http.UrlHelper.toUrl;
import static uk.nhs.hdn.common.http.client.connectionConfigurations.ChunkedUploadsConnectionConfiguration.DoesNotSupportChunkedUploads;

public final class Api extends AbstractToString
{
	private static final char Slash = '/';

	@NotNull
	public static final Api DataGovUk = new Api(false, "data.gov.uk", "");

	private final boolean useHttps;
	@NotNull @NonNls
	private final String domainName;
	private final char portNumber;
	@NotNull @NonNls
	private final String absoluteUrlPath;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public Api(final boolean useHttps, @NonNls @NotNull final String domainName, @NonNls @NotNull final String absoluteUrlPath)
	{
		this(useHttps, domainName, commonPortNumber(useHttps), absoluteUrlPath);
	}

	public Api(final boolean useHttps, @NonNls @NotNull final String domainName, final char portNumber, @NotNull final String absoluteUrlPath)
	{
		this.useHttps = useHttps;
		this.domainName = domainName;
		this.portNumber = portNumber;
		this.absoluteUrlPath = absoluteUrlPath;
	}

	@NotNull
	public ApiMethod<DatasetNameString[]> allDatasetNames()
	{
		return newApi(DatasetNamesSchemaInstance, "api", "1", "rest", "dataset");
	}

	@NotNull
	public ApiMethod<DatasetId[]> allDatasetIds()
	{
		return newApi(DatasetIdsUuidSchemaInstance, "api", "2", "rest", "dataset");
	}

	@NotNull
	public ApiMethod<GroupNameString[]> allGroupNames()
	{
		return newApi(GroupNamesSchemaInstance, "api", "1", "rest", "group");
	}

	@NotNull
	public ApiMethod<GroupId[]> allGroupIds()
	{
		return newApi(GroupIdsUuidSchemaInstance, "api", "2", "rest", "group");
	}

	@NotNull
	public ApiMethod<TagString[]> allTags()
	{
		return newApi(TagsSchemaInstance, "api", "2", "rest", "tag");
	}

	@NotNull
	public ApiMethod<Licence[]> allLicences()
	{
		return newApi(LicencesSchemaInstance, "api", "2", "rest", "licenses");
	}

	@NotNull
	public <V> ApiMethod<V> newApi(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String... urlPieces)
	{
		final StringBuilder stringBuilder = new StringBuilder(absoluteUrlPath);
		for (final String urlPiece : urlPieces)
		{
			stringBuilder.append(Slash).append(urlPiece);
		}

		final String urlPath = stringBuilder.toString();
		final HttpClient httpClient = new JavaHttpClient(toUrl(useHttps, domainName, portNumber, urlPath), DoesNotSupportChunkedUploads);
		return new ApiMethod<>(httpClient, jsonSchema);
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

		final Api that = (Api) obj;

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
