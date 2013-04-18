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

package uk.nhs.hdn.crds.store.rest.metadata;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.AbstractSubResource;
import uk.nhs.hdn.common.serialisers.json.JsonSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.xml.XmlSerialiser;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;

import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.*;
import static uk.nhs.hdn.common.http.server.sun.helpers.ByteArrayResourceContentHelper.resourceContent;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArraysResourceContent.jsonpByteArraysResourceContent;
import static uk.nhs.hdn.crds.store.rest.metadata.AllFormatsQueryStringEventHandler.parseAllFormatsQueryStringEventHandler;

public final class MetadataRecordSubResource extends AbstractSubResource
{
	public static final int FourKilobytes = 4096;
	private final ByteArrayResourceContent jsonUtf8Content;
	private final ByteArrayResourceContent xmlUtf8Content;
	private final ByteArrayResourceContent tsvUtf8Content;
	private final ByteArrayResourceContent csvUtf8Content;

	@SuppressWarnings("FeatureEnvy")
	public MetadataRecordSubResource(@MillisecondsSince1970 final long lastModified, @NonNls @NotNull final String xmlRootElementName, @SuppressWarnings("TypeMayBeWeakened") @NotNull final SeparatedValueSerialiser tsvSerialiser, @SuppressWarnings("TypeMayBeWeakened") @NotNull final SeparatedValueSerialiser csvSerialiser, @NotNull final AbstractMetadataRecord<?>... metadataRecordOrRecords)
	{
		super(lastModified);
		final int sizeGuess = FourKilobytes * metadataRecordOrRecords.length;
		jsonUtf8Content = resourceContent(JsonContentTypeUtf8, new JsonSerialiser(), sizeGuess, metadataRecordOrRecords);
		xmlUtf8Content = resourceContent(XmlContentTypeUtf8, new XmlSerialiser(true, xmlRootElementName), sizeGuess * 4, metadataRecordOrRecords);
		tsvUtf8Content = resourceContent(TsvContentTypeUtf8, tsvSerialiser, sizeGuess, metadataRecordOrRecords);
		csvUtf8Content = resourceContent(CsvContentTypeUtf8, csvSerialiser, sizeGuess, metadataRecordOrRecords);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	@Override
	public ResourceContent content(@Nullable final String rawQueryString) throws BadRequestException
	{
		final AllFormatsQueryStringEventHandler queryStringEventHandler = parseAllFormatsQueryStringEventHandler(rawQueryString);

		if (queryStringEventHandler.isXml())
		{
			return xmlUtf8Content;
		}
		if (queryStringEventHandler.isTsv())
		{
			return tsvUtf8Content;
		}
		if (queryStringEventHandler.isCsv())
		{
			return csvUtf8Content;
		}
		if (queryStringEventHandler.isJsonP())
		{
			return jsonpByteArraysResourceContent(queryStringEventHandler.jsonpFunctionName(), jsonUtf8Content);
		}
		return jsonUtf8Content;
	}
}
