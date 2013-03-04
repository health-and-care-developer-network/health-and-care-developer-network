package uk.nhs.hdn.dbs.request.client;

import uk.nhs.hdn.common.postCodes.AbstractPostCode;
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.dbs.*;
import uk.nhs.hdn.dbs.request.Request;
import uk.nhs.hdn.dbs.request.RequestHeader;
import uk.nhs.hdn.dbs.request.requestBodies.RequestBody;
import uk.nhs.hdn.dbs.request.requestBodies.StandardSearchRequestBody;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.out;
import static uk.nhs.hdn.dbs.FileVersion.PracticeAndAddressDataReturnedAlternative;

public class Example3
{
	public void example() throws IOException, CouldNotEncodeDataException
	{
		final OutputStream outputStream = out;
		final SpineDirectoryServiceOrganisationCode requestingOrganisationCode = new SpineDirectoryServiceOrganisationCode("Some code");
		final TracingServiceCode tracingServiceCode = new TracingServiceCode("some code");
		final int fileSequenceNumber = 1; // range is 1 to RequestHeader.MaximumFileSequenceNumber

		final LocalPatientIdentifier localPatientIdentifier = new LocalPatientIdentifier("Some string value of PID");
		final DbsDate dateOfBirth = DbsDate.valueOf("20130210"); // YYYYMMDD

		final NameFragment familyName = new NameFragment("Cohn");
		final NameFragment givenName = new NameFragment("Raphael");
		final Gender administrativeGender = Gender.Male;
		final PostCode postCode = AbstractPostCode.valueOf("LS1 4BY");
		final RequestBody requestBody = new StandardSearchRequestBody(localPatientIdentifier, dateOfBirth, familyName, givenName, administrativeGender, postCode);
		final RequestHeader requestHeader = new RequestHeader(PracticeAndAddressDataReturnedAlternative, requestingOrganisationCode, tracingServiceCode, fileSequenceNumber);
		final Request request = new Request(requestHeader, requestBody);
		request.serialise(outputStream);
	}
}
