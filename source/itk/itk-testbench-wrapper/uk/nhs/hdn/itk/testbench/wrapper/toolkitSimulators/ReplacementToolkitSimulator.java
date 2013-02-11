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

package uk.nhs.hdn.itk.testbench.wrapper.toolkitSimulators;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.warlock.tk.boot.ToolkitSimulator;

import java.util.Set;

import static java.util.Collections.emptySet;

public final class ReplacementToolkitSimulator extends AbstractDeprecatedToolkitSimulator
{
	@NotNull
	public static final ToolkitSimulator ReplacementToolkitSimulatorInstance = new ReplacementToolkitSimulator();

	private ReplacementToolkitSimulator()
	{
	}

	@NonNls
	@NotNull
	@Override
	public String getConfigurationName()
	{
		return "ITK Testbench Configuration";
	}

	@NotNull
	@Override
	public String getVersion()
	{
		return "2.0.0";
	}

	@NotNull
	@Override
	public String getOrganisationName()
	{
		return "HDN";
	}

	// Effectively called by dead code
	@NotNull
	@Override
	public Set<String> getServiceNames()
	{
		return emptySet();
	}
}
