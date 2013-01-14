/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1;

import org.junit.Test;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.Serialiser;
import uk.nhs.hcdn.common.serialisers.json.JsonPSerialiser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix.gs1CompanyPrefix;
import static uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.AdditionalInformation.additionalInformation;
import static uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.AdditionalInformationKey.PostCode;

public final class ToJson
{
	@SuppressWarnings("MultipleExceptionsDeclaredOnTestMethod")
	@Test
	public void x() throws IOException, CouldNotSerialiseMapException, CouldNotWriteDataException, CouldNotWriteValueException
	{
		final Tuple tuple = new Tuple(gs1CompanyPrefix("50552071"), "Cambridge University Hospitals NHS Foundation Trust", "Addenbrooke's Hospital", additionalInformation(PostCode.with("CB2 0QQ")));

		final Tuple[] tuples = new Tuple[] {tuple};

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
		try
		{
			final Serialiser serialiser = new JsonPSerialiser(byteArrayOutputStream, "jsonp");
			try
			{
				serialiser.start();
				serialiser.writeValue(tuples);
			}
			finally
			{
				serialiser.finish();
			}
		}
		finally
		{
			byteArrayOutputStream.close();
			final String s = byteArrayOutputStream.toString("UTF-8");
			System.out.println(s);
		}
	}
}
