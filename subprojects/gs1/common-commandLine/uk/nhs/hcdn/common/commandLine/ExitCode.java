/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.commandLine;

import static java.lang.Runtime.getRuntime;

public enum ExitCode
{
	Ok(0),
	GeneralError(1),
	Help(2),
	;

	private final int exitCode;

	@SuppressWarnings("MagicNumber")
	ExitCode(final int exitCode)
	{
		this.exitCode = exitCode;
		if (exitCode < 0 || exitCode > 255)
		{
			throw new IllegalArgumentException("ExitCode must be between 0 and 255");
		}
	}

	@SuppressWarnings("CallToSystemExit")
	public void exit()
	{
		getRuntime().exit(exitCode);
	}
}
