package uk.nhs.hdn.dbs.request.client;

import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.dbs.DbsDate;
import uk.nhs.hdn.dbs.LocalPatientIdentifier;
import uk.nhs.hdn.dbs.SpineDirectoryServiceOrganisationCode;
import uk.nhs.hdn.dbs.TracingServiceCode;
import uk.nhs.hdn.dbs.request.Request;
import uk.nhs.hdn.dbs.request.RequestHeader;
import uk.nhs.hdn.dbs.request.requestBodies.CrossCheck2RequestBody;
import uk.nhs.hdn.dbs.request.requestBodies.RequestBody;
import uk.nhs.hdn.number.NhsNumber;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.out;
import static uk.nhs.hdn.dbs.FileVersion.NoPracticeOrPatientAddressReturned;

public class Example2
{
	public void example() throws IOException, CouldNotEncodeDataException
	{
		final OutputStream outputStream = out;
		final SpineDirectoryServiceOrganisationCode requestingOrganisationCode = new SpineDirectoryServiceOrganisationCode("Some code");
		final TracingServiceCode tracingServiceCode = new TracingServiceCode("some code");
		final int fileSequenceNumber = 1; // range is 1 to RequestHeader.MaximumFileSequenceNumber

		final LocalPatientIdentifier localPatientIdentifier = new LocalPatientIdentifier("Some string value of PID");
		final DbsDate dateOfBirth = DbsDate.valueOf("20130210"); // YYYYMMDD
		final NhsNumber nhsNumber = NhsNumber.valueOf("1234567890"); // Also variants with separators such as "123 456 7890" and "123-456-7890" are supported

		final String familyNameFirstThreeCharacters = "Coh";
		final char givenNameFirstCharacter = 'R';
		final RequestBody requestBody = new CrossCheck2RequestBody(localPatientIdentifier, dateOfBirth, nhsNumber, familyNameFirstThreeCharacters, givenNameFirstCharacter);
		final RequestHeader requestHeader = new RequestHeader(NoPracticeOrPatientAddressReturned, requestingOrganisationCode, tracingServiceCode, fileSequenceNumber);
		final Request request = new Request(requestHeader, requestBody);
		request.serialise(outputStream);
	}
}
