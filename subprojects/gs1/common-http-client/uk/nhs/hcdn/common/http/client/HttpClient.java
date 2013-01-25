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

package uk.nhs.hcdn.common.http.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hcdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hcdn.common.http.client.exceptions.CouldNotUploadException;
import uk.nhs.hcdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hcdn.common.http.client.getHttpResponseUsers.GetHttpResponseUser;
import uk.nhs.hcdn.common.http.client.headHttpResponseUsers.HeadHttpResponseUser;
import uk.nhs.hcdn.common.http.client.uploadContents.UploadContent;

public interface HttpClient
{
	void head(@NotNull final HeadHttpResponseUser headHttpResponseUser) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException;

	@NotNull
	<V> V get(@NotNull final GetHttpResponseUser<V> getHttpResponseUser) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException;

	@NotNull
	<V> V post(@NotNull final UploadContent uploadContent, @NotNull final GetHttpResponseUser<V> getHttpResponseUser) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException, CouldNotUploadException;
}
