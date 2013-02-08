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

package uk.nhs.hdn.dbs.request.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.dbs.request.Request;
import uk.nhs.dbs.request.RequestHeader;
import uk.nhs.dbs.request.requestBodies.CrossCheck1RequestBody;
import uk.nhs.dbs.request.requestBodies.RequestBody;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.dbs.DbsDate;
import uk.nhs.hdn.dbs.LocalPatientIdentifier;
import uk.nhs.hdn.dbs.SpineDirectoryServiceOrganisationCode;
import uk.nhs.hdn.dbs.TracingServiceCode;
import uk.nhs.hdn.number.NhsNumber;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.out;
import static uk.nhs.dbs.request.RequestHeader.MaximumFileSequenceNumber;
import static uk.nhs.hdn.dbs.DbsDate.DbsDateFormat;
import static uk.nhs.hdn.dbs.FileVersion.NoPracticeOrPatientAddressReturned;

public final class DbsRequestClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String OutOption = "out";
	private static final String RequestingOrganisationCodeOption = "organisation";
	private static final String TracingServiceCodeOption = "tracing-service-code";
	private static final String FileSequenceNumberOption = "file-sequence-number";
	private static final String LocalPatientIdentifierOption = "local-patient-identifier";
	private static final String DateOfBirthOption = "dob";
	private static final String NhsNumberOption = "nhs-number";

	@NonNls
	private static final String OutDefault = "-";
	private static final Integer FileSequenceNumberDefault = 1;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(DbsRequestClientConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(OutOption, "Path to output file").withRequiredArg().ofType(String.class).defaultsTo(OutDefault).describedAs("Path to output file, or - to use standard out (the default)");
		options.accepts(RequestingOrganisationCodeOption, "SDS Organisation Code").withRequiredArg().ofType(SpineDirectoryServiceOrganisationCode.class).describedAs("SDS Organisation Code");
		options.accepts(TracingServiceCodeOption, "?").withRequiredArg().ofType(TracingServiceCode.class).describedAs("Tracing Service Code");
		options.accepts(FileSequenceNumberOption, "defaults to 1").withRequiredArg().ofType(Integer.class).defaultsTo(FileSequenceNumberDefault).describedAs("defaults to 1");
		options.accepts(LocalPatientIdentifierOption, "Local PID").withRequiredArg().ofType(LocalPatientIdentifier.class).describedAs("Local PID");
		options.accepts(DateOfBirthOption, "Date of Birth as " + DbsDateFormat).withRequiredArg().ofType(DbsDate.class).describedAs("Date of Birth as " + DbsDateFormat);
		options.accepts(NhsNumberOption, "10 digit NHS Number").withRequiredArg().ofType(NhsNumber.class).describedAs("10 digit NHS Number");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException, CouldNotEncodeDataException
	{
		final String path = defaulted(optionSet, OutOption);
		final SpineDirectoryServiceOrganisationCode requestingOrganisationCode = required(optionSet, RequestingOrganisationCodeOption);
		final TracingServiceCode tracingServiceCode = required(optionSet, TracingServiceCodeOption);
		final int fileSequenceNumber = unsignedInteger(optionSet, FileSequenceNumberOption);
		if (fileSequenceNumber > MaximumFileSequenceNumber)
		{
			exitWithErrorAndHelp(FileSequenceNumberOption, fileSequenceNumber, "must be between 1 and 99999999");
		}
		final LocalPatientIdentifier localPatientIdentifier = required(optionSet, LocalPatientIdentifierOption);
		final DbsDate dateOfBirth = required(optionSet, DateOfBirthOption);
		final NhsNumber nhsNumber = required(optionSet, NhsNumberOption);
		final OutputStream outputStream = chooseOutputStream(path);

		crossCheckNhsNumber(requestingOrganisationCode, tracingServiceCode, fileSequenceNumber, localPatientIdentifier, dateOfBirth, nhsNumber, outputStream);
	}

	private static void crossCheckNhsNumber(final SpineDirectoryServiceOrganisationCode requestingOrganisationCode, final TracingServiceCode tracingServiceCode, final int fileSequenceNumber, final LocalPatientIdentifier localPatientIdentifier, final DbsDate dateOfBirth, final NhsNumber nhsNumber, final OutputStream outputStream) throws IOException, CouldNotEncodeDataException
	{
		final RequestBody requestBody = new CrossCheck1RequestBody(localPatientIdentifier, dateOfBirth, nhsNumber);
		final Request request = new Request(new RequestHeader(NoPracticeOrPatientAddressReturned, requestingOrganisationCode, tracingServiceCode, fileSequenceNumber), requestBody);

		try
		{
			request.serialise(outputStream);
		}
		finally
		{
			outputStream.close();
		}
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static OutputStream chooseOutputStream(final String path) throws FileNotFoundException
	{
		final OutputStream outputStream;
		if (path.equals(OutDefault))
		{
			outputStream = out;
		}
		else
		{
			outputStream = new FileOutputStream(path);
		}
		return outputStream;
	}
}
