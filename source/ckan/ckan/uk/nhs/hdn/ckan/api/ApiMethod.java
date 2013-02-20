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

package uk.nhs.hdn.ckan.api;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.getHttpResponseUsers.GetHttpResponseUser;
import uk.nhs.hdn.common.http.client.json.JsonGetHttpResponseUser;
import uk.nhs.hdn.common.parsers.json.JsonSchema;

public final class ApiMethod<V>
{
	@NotNull
	private final HttpClient httpClient;
	@NotNull
	private final GetHttpResponseUser<V> getHttpResponseUser;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public ApiMethod(@NotNull final HttpClient httpClient, @NotNull final JsonSchema<V> jsonSchema)
	{
		this.httpClient = httpClient;
		getHttpResponseUser = new JsonGetHttpResponseUser<>(jsonSchema);
	}

	@NotNull
	public V get() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		return httpClient.get(getHttpResponseUser);
	}
}
