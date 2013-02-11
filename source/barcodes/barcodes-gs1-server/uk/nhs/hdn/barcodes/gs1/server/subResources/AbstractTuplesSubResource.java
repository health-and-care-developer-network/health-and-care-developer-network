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
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.subResources.SubResource;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.json.JsonPSerialiser;
import uk.nhs.hdn.common.serialisers.json.JsonSerialiser;
import uk.nhs.hdn.common.serialisers.xml.XmlSerialiser;

import static uk.nhs.hdn.barcodes.gs1.organisation.Tuple.*;
import static uk.nhs.hdn.barcodes.gs1.server.Gs1CompanyPrefxQueryStringEventHandler.parseGs1QueryString;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.*;
import static uk.nhs.hdn.common.http.server.sun.helpers.ByteArrayResourceContentHelper.resourceContent;

public abstract class AbstractTuplesSubResource extends AbstractToString implements SubResource
{

	private final int guess;

	private final Tuple[] tuples;
	private final ByteArrayResourceContent jsonUtf8Content;
	private final ByteArrayResourceContent xmlUtf8Content;
	private final ByteArrayResourceContent tsvUtf8Content;
	private final ByteArrayResourceContent csvUtf8Content;

	@SuppressWarnings("FeatureEnvy")
	protected AbstractTuplesSubResource(final int guess, @NotNull final Tuple... tuples)
	{
		this.tuples = copyOf(tuples);
		this.guess = guess;
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
			// TODO: A more efficient design is to copy the bytes of json utf-8, and use its size as an input instead of guess
			return resourceContent(JsonContentTypeUtf8, new JsonPSerialiser(queryStringEventHandler.jsonpFunctionName()), guess, tuples);
		}
		return jsonUtf8Content;
	}

}
