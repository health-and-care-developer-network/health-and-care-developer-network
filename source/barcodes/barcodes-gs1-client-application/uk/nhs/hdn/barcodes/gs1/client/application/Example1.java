package uk.nhs.hdn.barcodes.gs1.client.application;

import uk.nhs.hdn.barcodes.gs1.client.ClientApplication;
import uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.barcodes.gs1.organisation.AdditionalInformationKey.PostCode;

public class Example1
{
	public void example() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final ClientApplication clientApplication = new ClientApplication();
		final Tuple[] tuples = clientApplication.listAllKnownCompanyPrefixes();
		for (final Tuple tuple : tuples)
		{
			final Gs1CompanyPrefix gs1CompanyPrefix = tuple.gs1CompanyPrefix();
			final String organisationName = tuple.organisationName();
			final String trust = tuple.trust();

			final Gs1Prefix gs1Prefix = gs1CompanyPrefix.gs1Prefix();

			final String postCode = (String) tuple.additionalInformationFor(PostCode);
		}
	}
}
