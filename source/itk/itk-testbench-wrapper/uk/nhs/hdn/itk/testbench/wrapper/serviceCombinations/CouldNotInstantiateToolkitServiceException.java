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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotInstantiateToolkitServiceException extends Exception
{
	public CouldNotInstantiateToolkitServiceException(@NotNull final Class<? extends ToolkitService> toolkitServiceClass, @NotNull final Throwable cause)
	{
		super(format(ENGLISH, "Could not instantiate toolkit service %1$s because of exception %2$s", toolkitServiceClass.getSimpleName(), cause.getMessage()), cause);
	}
}
