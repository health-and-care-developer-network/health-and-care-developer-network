package uk.nhs.hdn.dts.client.read;

import uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier;
import uk.nhs.hdn.dts.domain.fileNames.FileName;

import java.io.File;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.dts.domain.fileNames.FileExtension.ctl;
import static uk.nhs.hdn.dts.domain.fileNames.FileName.parseFileName;

public class Example2
{
	public void example()
	{
		final String filePath = "/PATH/TO/CONTROL/FILE";
		final File controlFile = new File(filePath);
		final String name = controlFile.getName();

		final FileName fileName = parseFileName(name);
		if (!fileName.hasFileExtension(ctl))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Control file named %1$s (in file %2$s) does not have a .ctl extension", fileName, controlFile));
		}

		final String siteIdentifierIncludingAppName = fileName.siteIdentifierIncludingApp();
		final TransactionSequenceIdentifier sequenceIdentifier = fileName.sequenceIdentifier();
	}
}
