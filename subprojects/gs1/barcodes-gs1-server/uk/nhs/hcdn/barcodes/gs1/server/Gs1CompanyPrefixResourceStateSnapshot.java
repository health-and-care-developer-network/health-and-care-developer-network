/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.AbstractResourceStateSnapshot;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.resourceContents.ByteArrayResourceContent;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.resourceContents.ResourceContent;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.json.JsonSerialiser;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.GregorianCalendar;

import static java.nio.charset.Charset.forName;

public final class Gs1CompanyPrefixResourceStateSnapshot extends AbstractResourceStateSnapshot
{
	private static final int Guess = 4096;
	private static final Charset Utf8 = forName("UTF-8");
	private final ByteArrayResourceContent jsonUtf8Content;

	public Gs1CompanyPrefixResourceStateSnapshot(@NotNull final GregorianCalendar lastModified, @NotNull final Tuple... tuples)
	{
		super(lastModified);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Guess);
		final JsonSerialiser jsonSerialiser = new JsonSerialiser(byteArrayOutputStream, Utf8);
		try
		{
			jsonSerialiser.writeValue(tuples);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		jsonUtf8Content = new ByteArrayResourceContent("application/json;charset=utf-8", byteArrayOutputStream.toByteArray());
	}

	@NotNull
	@Override
	public ResourceContent content(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString)
	{
		return jsonUtf8Content;
	}
}
