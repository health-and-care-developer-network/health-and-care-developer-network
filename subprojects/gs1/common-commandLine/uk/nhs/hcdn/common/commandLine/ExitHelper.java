/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
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
	@NotNull
	private final OptionParser options;

	public ExitHelper(@NotNull final OptionParser options)
	{
		this.options = options;
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void exitWithHelp() throws IOException
	{
		options.printHelpOn(err);
		Help.exit();
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void exitWithErrorAndHelp(@NonNls @NotNull final String template, @NonNls @NotNull final Object... values) throws IOException
	{
		exitWithErrorAndHelp(format(ENGLISH, template, values));
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public void exitWithErrorAndHelp(@NonNls @NotNull final String message) throws IOException
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
