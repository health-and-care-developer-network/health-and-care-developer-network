package uk.nhs.hdn.barcodes.gs1.client.application;

import uk.nhs.hdn.barcodes.gs1.client.ClientApplication;
import uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.barcodes.gs1.organisation.AdditionalInformationKey.PostCode;

public class Example2
{
	public void example() throws CorruptResponseException, CouldNotConnectHttpException, UnacceptableResponseException
	{
		final ClientApplication clientApplication = new ClientApplication();
		final Tuple tuple;
		try
		{
			tuple = clientApplication.listCompanyPrefixForGlobalTradeItemNumber("5055220798768");
		}
		catch (UnacceptableResponseException e)
		{
			if (e.isNotFound())
			{
				return;
			}
			throw e;
		}

		final Gs1CompanyPrefix gs1CompanyPrefix = tuple.gs1CompanyPrefix();
		final String organisationName = tuple.organisationName();
		final String trust = tuple.trust();

		final Gs1Prefix gs1Prefix = gs1CompanyPrefix.gs1Prefix();

		final String postCode = (String) tuple.additionalInformationFor(PostCode);
	}
}
