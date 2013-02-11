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

package uk.nhs.hdn.itk.testbench.wrapper.toolkitServiceInstantiators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.warlock.tk.boot.ToolkitService;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.itk.testbench.wrapper.serviceCombinations.CouldNotInstantiateToolkitServiceException;
import uk.nhs.hdn.itk.testbench.wrapper.serviceCombinations.GenericToolkitServiceInstantiator;

import java.util.Arrays;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public abstract class AbstractToolkitServiceConstructor<S extends ToolkitService> extends AbstractToString implements ToolkitServiceConstructor<S>
{
	@NotNull
	private final Class<S> toolkitServiceClass;
	@NotNull
	private final Pair<String, String>[] serviceProperties;

	@SafeVarargs
	protected AbstractToolkitServiceConstructor(@NotNull final Class<S> toolkitServiceClass, @NotNull final Pair<String, String>... serviceProperties)
	{
		this.toolkitServiceClass = toolkitServiceClass;
		this.serviceProperties = copyOf(serviceProperties);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractToolkitServiceConstructor<?> that = (AbstractToolkitServiceConstructor<?>) obj;

		if (!Arrays.equals(serviceProperties, that.serviceProperties))
		{
			return false;
		}
		if (!toolkitServiceClass.equals(that.toolkitServiceClass))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = toolkitServiceClass.hashCode();
		result = 31 * result + Arrays.hashCode(serviceProperties);
		return result;
	}

	@Override
	@NotNull
	public final S instantiate(@NotNull final GenericToolkitServiceInstantiator genericToolkitServiceInstantiator) throws CouldNotInstantiateToolkitServiceException
	{
		return genericToolkitServiceInstantiator.instantiateToolkitService(toolkitServiceClass, serviceProperties);
	}
}
