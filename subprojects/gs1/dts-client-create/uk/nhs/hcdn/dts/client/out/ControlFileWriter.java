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

package uk.nhs.hcdn.dts.client.out;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hcdn.dts.domain.*;
import uk.nhs.hcdn.dts.domain.fileNames.FileName;
import uk.nhs.hcdn.dts.domain.identifiers.DtsIdentifier;
import uk.nhs.hcdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hcdn.dts.domain.identifiers.WorkflowIdentifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createLink;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static uk.nhs.hcdn.dts.domain.ControlFile.dataControlFile;
import static uk.nhs.hcdn.dts.domain.fileNames.FileExtension.ctl;
import static uk.nhs.hcdn.dts.domain.schema.ControlFileSchemaParser.serialiseControlFile;

public final class ControlFileWriter
{
	@NotNull
	private final OutputStream outputStream;

	public ControlFileWriter(@NotNull final OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public static OutputStream copyDataFileAndCreateEmptyControlFile(@NotNull final File outFolder, @NotNull final File inDataFile, @NotNull final String siteIdentifierIncludingApp, @NotNull final TransactionSequenceIdentifier sequenceIdentifier) throws IOException
	{
		final FileName fileName = new FileName(siteIdentifierIncludingApp, sequenceIdentifier, ctl);
		final File outControlFile = fileName.onPath(outFolder);
		if (outControlFile.exists())
		{
			throw new IOException("Control file already exists");
		}
		final Path dataFileTo = fileName.toDataFileName().onPath(outFolder.toPath());
		final Path dataFileFrom = inDataFile.toPath();
		try
		{
			createLink(dataFileTo, dataFileFrom);
		}
		catch (FileAlreadyExistsException e)
		{
			throw e;
		}
		catch (IOException ignored)
		{
			copy(dataFileFrom, dataFileTo, COPY_ATTRIBUTES);
		}
		return new FileOutputStream(outControlFile);
	}

	public void writeControlFile(@NotNull final WorkflowIdentifier workflowIdentifier, @NotNull final Subject subject, @NotNull final LocalIdentifier localIdentifier, @NotNull final DtsIdentifier dtsIdentifier, @NotNull final ContentEncoding contentEncoding, @NotNull final AddressPair<SmtpAddress> smtpAddresses, @NotNull final AddressPair<DtsName> dtsNames) throws CouldNotSerialiseException
	{
		final ControlFile controlFile = dataControlFile(workflowIdentifier, subject, localIdentifier, dtsIdentifier, contentEncoding, smtpAddresses, dtsNames);

		boolean firstTimeExceptionThrown = false;
		try
		{
			serialiseControlFile(controlFile, outputStream);
			firstTimeExceptionThrown = true;
		}
		finally
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				if (firstTimeExceptionThrown)
				{
					throw new CouldNotSerialiseException(e);
				}
			}
		}
	}
}
