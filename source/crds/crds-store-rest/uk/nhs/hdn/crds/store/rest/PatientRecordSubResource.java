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

package uk.nhs.hdn.crds.store.rest;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.AbstractSubResource;
import uk.nhs.hdn.common.serialisers.json.JsonSerialiser;
import uk.nhs.hdn.common.serialisers.xml.XmlSerialiser;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;

import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.JsonContentTypeUtf8;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.XmlContentTypeUtf8;
import static uk.nhs.hdn.common.http.server.sun.helpers.ByteArrayResourceContentHelper.resourceContent;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArraysResourceContent.jsonpByteArraysResourceContent;
import static uk.nhs.hdn.crds.store.rest.PatientRecordStoreQueryStringEventHandler.parseCrdsStoreQueryString;

public final class PatientRecordSubResource extends AbstractSubResource
{
	private final ByteArrayResourceContent jsonUtf8Content;
	private final ByteArrayResourceContent xmlUtf8Content;

	@SuppressWarnings("FeatureEnvy")
	public PatientRecordSubResource(@MillisecondsSince1970 final long lastModified, final int guess, @NonNls @NotNull final String xmlRootElementName, @NotNull final SimplePatientRecord patientRecord)
	{
		super(lastModified);
		jsonUtf8Content = resourceContent(JsonContentTypeUtf8, new JsonSerialiser(), guess, patientRecord);
		xmlUtf8Content = resourceContent(XmlContentTypeUtf8, new XmlSerialiser(true, xmlRootElementName), guess * 4, patientRecord);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	@Override
	public ResourceContent content(@Nullable final String rawQueryString) throws BadRequestException
	{
		final PatientRecordStoreQueryStringEventHandler queryStringEventHandler = parseCrdsStoreQueryString(rawQueryString);

		if (queryStringEventHandler.isXml())
		{
			return xmlUtf8Content;
		}
		if (queryStringEventHandler.isJsonP())
		{
			return jsonpByteArraysResourceContent(queryStringEventHandler.jsonpFunctionName(), jsonUtf8Content);
		}
		return jsonUtf8Content;
	}

}
