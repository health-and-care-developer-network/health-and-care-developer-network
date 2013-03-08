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

package uk.nhs.hdn.dts.client.out;

import uk.nhs.hdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hdn.dts.domain.*;
import uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.WorkflowIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static uk.nhs.hdn.dts.client.out.ControlFileWriter.copyDataFileAndCreateEmptyControlFile;
import static uk.nhs.hdn.dts.domain.AddressPair.UnknownDtsNames;
import static uk.nhs.hdn.dts.domain.ContentEncoding.AsIs;
import static uk.nhs.hdn.dts.domain.Subject.UnknownSubject;
import static uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier.valueOf;
import static uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier.UnknownDtsIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier.UnknownLocalIdentifier;

public class Example1
{
	public void example() throws IOException, CouldNotSerialiseException
	{
		final File outFolder = new File("/path/to/dts/out/folder");
		final File inDataFile = new File("/path/to/data/file/to/send");
		final String siteIdentifierIncludingApp = "SOME_APP_NAME";
		final TransactionSequenceIdentifier sequenceIdentifier = valueOf("00000002");
		final OutputStream controlFileOutputStream = copyDataFileAndCreateEmptyControlFile(outFolder, inDataFile, siteIdentifierIncludingApp, sequenceIdentifier);

		final WorkflowIdentifier workflowIdentifier = new WorkflowIdentifier("workflow");
		final Subject subject = UnknownSubject;
		final LocalIdentifier localIdentifier = UnknownLocalIdentifier;
		final DtsIdentifier dtsIdentifier = UnknownDtsIdentifier;
		final ContentEncoding contentEncoding = AsIs;
		final AddressPair<SmtpAddress> smtpAddresses = new AddressPair<>(new SmtpAddress("raphael.cohn@stormmq.com"), new SmtpAddress("adam.hatherly@nhs.net"));
		final AddressPair<DtsName> dtsNames = UnknownDtsNames;

		final ControlFileWriter controlFileWriter = new ControlFileWriter(controlFileOutputStream);
		controlFileWriter.writeControlFile(workflowIdentifier, subject, localIdentifier, dtsIdentifier, contentEncoding, smtpAddresses, dtsNames);
	}
}
