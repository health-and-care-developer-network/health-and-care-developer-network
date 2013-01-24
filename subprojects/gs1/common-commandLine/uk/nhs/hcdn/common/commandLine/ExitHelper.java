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

package uk.nhs.hcdn.common.commandLine;

import joptsimple.OptionParser;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.commandLine.ExitCode.GeneralError;
import static uk.nhs.hcdn.common.commandLine.ExitCode.Help;

public final class ExitHelper extends AbstractToString
{
	private ExitHelper()
	{
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void exitWithHelp(@NotNull final OptionParser options) throws IOException
	{
		options.printHelpOn(err);
		Help.exit();
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void exitWithErrorAndHelp(@NotNull final OptionParser options, @NonNls @NotNull final String template, @NonNls @NotNull final Object... values) throws IOException
	{
		exitWithErrorAndHelp(options, format(ENGLISH, template, values));
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void exitWithErrorAndHelp(@NotNull final OptionParser options, @NonNls @NotNull final String message) throws IOException
	{
		err.println(message);
		options.printHelpOn(err);
		GeneralError.exit();
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public static void exitWithException(@NotNull final Exception e)
	{
		e.printStackTrace();
		GeneralError.exit();
	}
}
