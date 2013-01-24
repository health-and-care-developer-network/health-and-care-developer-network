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

package uk.nhs.hcdn.dts.client.read;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import uk.nhs.hcdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hcdn.common.commandLine.ShouldHaveExitedException;
import uk.nhs.hcdn.dts.domain.ControlFile;
import uk.nhs.hcdn.dts.domain.fileNames.FileName;

import java.io.*;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.dts.domain.fileNames.FileExtension.ctl;
import static uk.nhs.hcdn.dts.domain.fileNames.FileName.parseFileName;
import static uk.nhs.hcdn.dts.domain.schema.ControlFileSchemaParser.ControlFileSchemaParserInstance;

public final class ReadClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String InOption = "in";
	private static final String ValidateOption = "validate";

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(InOption).withRequiredArg().ofType(File.class).describedAs("path to a control file containing data");
		options.accepts(ValidateOption, "validate the control file name");
		return true;
	}

	@SuppressWarnings({"OverlyCoupledMethod", "OverlyLongMethod", "UseOfSystemOutOrSystemErr"})
	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException, SAXException
	{
		if (!optionSet.has(InOption))
		{
			exitWithErrorAndHelp("--in option required");
			throw new ShouldHaveExitedException();
		}

		final File controlFileFile = readableFile(optionSet, InOption);
		final boolean validateControlFileName = optionSet.has(ValidateOption);

		if (validateControlFileName)
		{
			validateControlFileName(controlFileFile);
		}

		parseControlFile(controlFileFile, out);
	}

	private void validateControlFileName(final File controFile)
	{
		final String name = controFile.getName();
		final FileName fileName;
		try
		{
			fileName = parseFileName(name);
		}
		catch(RuntimeException e)
		{
			exitWithErrorAndHelp(InOption, controFile, format(ENGLISH, "because the file name was not valid (%1$s)", e.getMessage()));
			throw new ShouldHaveExitedException(e);
		}
		if (!fileName.hasFileExtension(ctl))
		{
			exitWithErrorAndHelp(InOption, controFile, "because the file name did not end with extension .ctl");
			throw new ShouldHaveExitedException();
		}
	}

	private static void parseControlFile(final File controlFileFile, final PrintStream printStream) throws IOException, SAXException
	{
		final ControlFile controlFile;
		final InputStream inputStream = new FileInputStream(controlFileFile);
		try
		{
			controlFile = ControlFileSchemaParserInstance.parse(inputStream);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ignored)
			{
			}
		}
		printStream.println(controlFile);
		printStream.flush();
	}

	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(ReadClientConsoleEntryPoint.class, commandLineArguments);
	}
}
