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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.commandLine.ShouldHaveExitedException;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hdn.dts.domain.*;
import uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.domain.identifiers.WorkflowIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.getProperty;
import static java.lang.System.out;
import static uk.nhs.hdn.dts.client.out.ControlFileWriter.copyDataFileAndCreateEmptyControlFile;
import static uk.nhs.hdn.dts.domain.ContentEncoding.Compressed;
import static uk.nhs.hdn.dts.domain.DtsName.UnknownDtsName;
import static uk.nhs.hdn.dts.domain.SmtpAddress.UnknownSmtpAddress;
import static uk.nhs.hdn.dts.domain.Subject.UnknownSubject;
import static uk.nhs.hdn.dts.domain.TransactionSequenceIdentifier.One;
import static uk.nhs.hdn.dts.domain.identifiers.DtsIdentifier.UnknownDtsIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier.UnknownLocalIdentifier;
import static uk.nhs.hdn.dts.domain.identifiers.WorkflowIdentifier.UnknownWorkflowIdentifier;

public final class OutClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String WorkflowIdentifierOption = "workflow-identifier";
	private static final String SubjectOption = "subject";
	private static final String LocalIdentifierOption = "local-identifier";
	private static final String DtsIdentifierOption = "dts-identifier";
	private static final String ContentEncodingOption = "content-encoding";
	private static final String FromSmtpOption = "from-smtp";
	private static final String ToSmtpOption = "to-smtp";
	private static final String FromDtsNameOption = "from-dts-name";
	private static final String ToDtsNameOption = "to-dts-name";
	private static final String InOption = "in";
	private static final String OutOption = "out";
	private static final String SiteIdentifierOption = "site-identifier";
	private static final String SequenceIdentifierOption = "sequence-identifier";

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(WorkflowIdentifierOption).withRequiredArg().ofType(WorkflowIdentifier.class).defaultsTo(UnknownWorkflowIdentifier).describedAs("workflow identifier");
		options.accepts(SubjectOption).withRequiredArg().ofType(Subject.class).defaultsTo(UnknownSubject).describedAs("subject of data");
		options.accepts(LocalIdentifierOption).withRequiredArg().ofType(LocalIdentifier.class).defaultsTo(UnknownLocalIdentifier).describedAs("local identifier");
		options.accepts(DtsIdentifierOption).withRequiredArg().ofType(DtsIdentifier.class).defaultsTo(UnknownDtsIdentifier).describedAs("DTS identifier");
		options.accepts(ContentEncodingOption).withRequiredArg().ofType(ContentEncoding.class).defaultsTo(Compressed).describedAs("compress or encrypt");
		options.accepts(FromSmtpOption).withRequiredArg().ofType(SmtpAddress.class).defaultsTo(UnknownSmtpAddress).describedAs("from SMTP address");
		options.accepts(ToSmtpOption).requiredIf(FromSmtpOption).withRequiredArg().ofType(SmtpAddress.class).defaultsTo(UnknownSmtpAddress).describedAs("to SMTP address");
		options.accepts(FromDtsNameOption).withRequiredArg().ofType(DtsName.class).defaultsTo(UnknownDtsName).describedAs("from DTS name");
		options.accepts(ToDtsNameOption).requiredIf(FromDtsNameOption).withRequiredArg().ofType(DtsName.class).defaultsTo(UnknownDtsName).describedAs("to DTS name");
		options.accepts(OutOption).withRequiredArg().ofType(File.class).describedAs("a folder to create the control file in");
		options.accepts(InOption).requiredIf(OutOption).withRequiredArg().ofType(File.class).defaultsTo(new File(getProperty("user.dir"))).describedAs("path to a file containing data");
		options.accepts(SiteIdentifierOption).requiredIf(OutOption, InOption).withRequiredArg().ofType(String.class).describedAs("SiteId including APP");
		options.accepts(SequenceIdentifierOption).requiredIf(OutOption, InOption, SequenceIdentifierOption).withRequiredArg().ofType(TransactionSequenceIdentifier.class).defaultsTo(One).describedAs("transaction sequence identifier (zero padded)");
		return true;
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "OverlyCoupledMethod", "OverlyLongMethod"})
	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotSerialiseException, IOException
	{
		final WorkflowIdentifier workflowIdentifier = required(optionSet, WorkflowIdentifierOption);
		if (workflowIdentifier.isUnknown())
		{
			exitWithErrorAndHelp(WorkflowIdentifierOption, workflowIdentifier, "must be supplied");
			return;
		}
		final Subject subject = defaulted(optionSet, SubjectOption);
		final LocalIdentifier localIdentifier = defaulted(optionSet, LocalIdentifierOption);
		final DtsIdentifier dtsIdentifier = defaulted(optionSet, DtsIdentifierOption);
		final ContentEncoding contentEncoding = defaulted(optionSet, ContentEncodingOption);
		final SmtpAddress fromSmtpAddress = defaulted(optionSet, FromSmtpOption);
		final SmtpAddress toSmtpAddress = defaulted(optionSet, ToSmtpOption);
		final DtsName fromDtsName = defaulted(optionSet, FromDtsNameOption);
		final DtsName toDtsName = defaulted(optionSet, ToDtsNameOption);

		final AddressPair<SmtpAddress> smtpAddresses;
		final AddressPair<DtsName> dtsNames;
		try
		{
			smtpAddresses = new AddressPair<>(fromSmtpAddress, toSmtpAddress);
			dtsNames = new AddressPair<>(fromDtsName, toDtsName);
		}
		catch (IllegalArgumentException ignored)
		{
			exitWithErrorAndHelp("Please provide from and to for a set of SMTP addresses and / or DTS names");
			throw new ShouldHaveExitedException();
		}

		if (smtpAddresses.isUnknown() && dtsNames.isUnknown())
		{
			exitWithErrorAndHelp("Please provide from and to for either or SMTP addresses and DTS names");
			throw new ShouldHaveExitedException();
		}

		final OutputStream outputStream;
		if (optionSet.has(OutOption))
		{
			final File outFolder = writableDirectory(optionSet, OutOption);
			final File inDataFile = readableFile(optionSet, InOption);
			final String siteIdentifierIncludingApp = required(optionSet, SiteIdentifierOption);
			final TransactionSequenceIdentifier sequenceIdentifier = required(optionSet, SequenceIdentifierOption);

			outputStream = copyDataFileAndCreateEmptyControlFile(outFolder, inDataFile, siteIdentifierIncludingApp, sequenceIdentifier);
		}
		else
		{
			outputStream = out;
		}

		new ControlFileWriter(outputStream).writeControlFile(workflowIdentifier, subject, localIdentifier, dtsIdentifier, contentEncoding, smtpAddresses, dtsNames);
	}

	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(OutClientConsoleEntryPoint.class, commandLineArguments);
	}
}
