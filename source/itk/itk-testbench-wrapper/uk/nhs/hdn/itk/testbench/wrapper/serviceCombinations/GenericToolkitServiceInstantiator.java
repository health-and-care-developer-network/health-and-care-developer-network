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

package uk.nhs.hdn.itk.testbench.wrapper.serviceCombinations;

import org.jetbrains.annotations.NotNull;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ToolkitSimulator;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.itk.testbench.wrapper.serviceCombinations.ServiceNameHelper.serviceNameFromClassName;

public final class GenericToolkitServiceInstantiator extends AbstractToString
{
	@NotNull
	private final ToolkitSimulator replacementToolkitSimulator;

	public GenericToolkitServiceInstantiator(@NotNull final ToolkitSimulator replacementToolkitSimulator)
	{
		this.replacementToolkitSimulator = replacementToolkitSimulator;
	}

	@SuppressWarnings("FinalMethodInFinalClass") // safe args annotation requires final
	@SafeVarargs
	@NotNull
	public final <S extends ToolkitService> S instantiateToolkitService(@NotNull final Class<S> toolkitServiceClass, @NotNull final Pair<String, String>... serviceProperties) throws CouldNotInstantiateToolkitServiceException
	{
		final Constructor<S> constructor;
		try
		{
			constructor = toolkitServiceClass.getConstructor();
		}
		catch (NoSuchMethodException e)
		{
			throw new ShouldNeverHappenException(e);
		}

		final S toolkitService;
		try
		{
			toolkitService = constructor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (InvocationTargetException e)
		{
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new CouldNotInstantiateToolkitServiceException(toolkitServiceClass, e.getCause());
		}

		final Properties properties = new Properties();
		for (final Pair<String, String> serviceProperty : serviceProperties)
		{
			if (properties.setProperty(serviceProperty.a, serviceProperty.b) != null)
			{
				throw new IllegalArgumentException(format(ENGLISH, "Duplicate service property %1$s", serviceProperty.a));
			}
		}

		final String serviceName = serviceNameFromClassName(toolkitServiceClass);
		try
		{
			toolkitService.boot(replacementToolkitSimulator, properties, serviceName);
		}
		catch (Exception e)
		{
			throw new CouldNotInstantiateToolkitServiceException(toolkitServiceClass, e);
		}

		return toolkitService;
	}

}
