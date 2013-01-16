/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.http.queryString.InvalidQueryStringException;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.BadRequestException;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.AbstractResourceStateSnapshot;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ByteArrayResourceContent;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.MapSerialisable;
import uk.nhs.hcdn.common.serialisers.Serialiser;
import uk.nhs.hcdn.common.serialisers.json.JsonPSerialiser;
import uk.nhs.hcdn.common.serialisers.json.JsonSerialiser;
import uk.nhs.hcdn.common.serialisers.xml.XmlSerialiser;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.GregorianCalendar;

import static java.nio.charset.Charset.forName;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hcdn.common.http.queryString.QueryStringParser.parseQueryString;

public final class Gs1CompanyPrefixResourceStateSnapshot extends AbstractResourceStateSnapshot
{
	private static final int Guess = 4096;
	private static final Charset Utf8 = forName("UTF-8");
	private static final String JsonUtf8ContentType = "application/json;charset=utf-8";
	private static final String XmlUtf8ContentType = "application/xml;charset=utf-8";

	private final Tuple[] tuples;
	private final ByteArrayResourceContent jsonUtf8Content;
	private final ByteArrayResourceContent xmlUtf8Content;

	public Gs1CompanyPrefixResourceStateSnapshot(@NotNull final GregorianCalendar lastModified, @NotNull final Tuple... tuples)
	{
		super(lastModified);
		this.tuples = copyOf(tuples);
		jsonUtf8Content = resourceContent(JsonUtf8ContentType, new JsonSerialiser(), tuples);
		xmlUtf8Content = resourceContent(XmlUtf8ContentType, new XmlSerialiser("gs1"), tuples);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	@Override
	public ResourceContent content(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString) throws BadRequestException
	{
		final Gs1CompanyPrefxQueryStringEventHandler queryStringEventHandler = new Gs1CompanyPrefxQueryStringEventHandler();
		try
		{
			parseQueryString(rawQueryString, queryStringEventHandler);
		}
		catch (InvalidQueryStringException e)
		{
			throw new BadRequestException(e.getMessage(), e);
		}
		if (queryStringEventHandler.isXml())
		{
			return xmlUtf8Content;
		}
		if (queryStringEventHandler.isJsonP())
		{
			return resourceContent(JsonUtf8ContentType, new JsonPSerialiser(queryStringEventHandler.jsonp()), tuples);
		}
		return jsonUtf8Content;
	}

	@SafeVarargs
	private static <S extends MapSerialisable> ByteArrayResourceContent resourceContent(@NonNls @NotNull final String contentType, @NotNull final Serialiser serialiser, final S... values)
	{
		return new ByteArrayResourceContent(contentType, serialise(serialiser, values));
	}

	@SafeVarargs
	private static <S extends MapSerialisable> byte[] serialise(@NotNull final Serialiser serialiser, @NotNull final S... values)
	{
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Guess);
		try
		{
			serialiser.start(byteArrayOutputStream, Utf8);
			try
			{
				serialiser.writeValue(values);
			}
			catch (CouldNotWriteValueException e)
			{
				throw new ShouldNeverHappenException(e);
			}
			serialiser.finish();
		}
		catch (CouldNotWriteDataException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

}
