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

import org.jetbrains.annotations.NotNull;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ToolkitSimulator;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public abstract class AbstractDeprecatedToolkitSimulator extends AbstractToString implements ToolkitSimulator
{
	protected AbstractDeprecatedToolkitSimulator()
	{
	}

	@Deprecated
	@Override
	public final void initValidate()
	{
		throw new UnsupportedOperationException("initValidate is only used as if private by the original ToolkitSimulator");
	}

	@Deprecated
	@Override
	public final void initSpineValidate()
	{
		throw new UnsupportedOperationException("initSpineValidate is only used as if private by the original ToolkitSimulator");
	}

	@Deprecated
	@Override
	public final void initAutoTest()
	{
		throw new UnsupportedOperationException("initAutoTest is only used as if private by the original ToolkitSimulator");
	}

	@Deprecated
	@Override
	public final void initTransmit()
	{
		throw new UnsupportedOperationException("initTransmit is only used as if private by the original ToolkitSimulator");
	}

	@Deprecated
	@Override
	public final void boot()
	{
		throw new UnsupportedOperationException("boot is only used as if private by the original ToolkitSimulator");
	}

	@Deprecated
	@NotNull
	@Override
	public final ToolkitService getService(@NotNull final String sname)
	{
		throw new UnsupportedOperationException("getService is only used as if private by the original ToolkitSimulator");
	}
}
