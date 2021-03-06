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

package uk.nhs.hdn.barcodes.gs1.server.subResources;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.barcodes.gs1.server.Gs1CompanyPrefxQueryStringEventHandler;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.AbstractSubResource;
import uk.nhs.hdn.common.serialisers.json.JsonSerialiser;
import uk.nhs.hdn.common.serialisers.xml.XmlSerialiser;

import static uk.nhs.hdn.barcodes.gs1.organisation.Tuple.csvSerialiserForTuples;
import static uk.nhs.hdn.barcodes.gs1.organisation.Tuple.tsvSerialiserForTuples;
import static uk.nhs.hdn.barcodes.gs1.server.Gs1CompanyPrefxQueryStringEventHandler.parseGs1QueryString;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.*;
import static uk.nhs.hdn.common.http.server.sun.helpers.ByteArrayResourceContentHelper.resourceContent;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArraysResourceContent.jsonpByteArraysResourceContent;

public abstract class AbstractTuplesSubResource extends AbstractSubResource
{
	private final ByteArrayResourceContent jsonUtf8Content;
	private final ByteArrayResourceContent xmlUtf8Content;
	private final ByteArrayResourceContent tsvUtf8Content;
	private final ByteArrayResourceContent csvUtf8Content;

	@SuppressWarnings("FeatureEnvy")
	protected AbstractTuplesSubResource(@MillisecondsSince1970 final long lastModifiedTime, final int guess, @NotNull final Tuple... tuples)
	{
		super(lastModifiedTime);
		jsonUtf8Content = resourceContent(JsonContentTypeUtf8, new JsonSerialiser(), guess, tuples);
		xmlUtf8Content = resourceContent(XmlContentTypeUtf8, new XmlSerialiser(true, "gs1"), guess * 4, tuples);
		tsvUtf8Content = resourceContent(TsvContentTypeUtf8, tsvSerialiserForTuples(), guess * 4, tuples);
		// Note: does not check for IANA mime type attribute to include or exclude the heading
		csvUtf8Content = resourceContent(CsvContentTypeUtf8, csvSerialiserForTuples(true), guess * 4, tuples);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	@Override
	public final ResourceContent content(@Nullable final String rawQueryString) throws BadRequestException
	{
		final Gs1CompanyPrefxQueryStringEventHandler queryStringEventHandler = parseGs1QueryString(rawQueryString);

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
