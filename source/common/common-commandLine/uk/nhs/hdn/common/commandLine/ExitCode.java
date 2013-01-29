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

package uk.nhs.hdn.common.commandLine;

import org.jetbrains.annotations.NotNull;

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

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return Integer.toString(exitCode);
	}
}
