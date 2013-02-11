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
import org.warlock.tk.internalservices.SpineValidatorService;

import java.io.File;

import static uk.nhs.hdn.common.tuples.Pair.pair;

public final class SpineValidatorServiceConstructor extends AbstractToolkitServiceConstructor<SpineValidatorService>
{
	@SuppressWarnings({"DuplicateStringLiteralInspection", "FeatureEnvy"})
	public SpineValidatorServiceConstructor(@NotNull final File sourceDirectory, @NotNull final File reportDirectory, @NotNull final File validatorConfigurationFile)
	{
		super
		(
			SpineValidatorService.class,

			// Used by SpineValidatorService
			pair("tks.validator.source", sourceDirectory.toString()),
			pair("tks.validator.reports", reportDirectory.toString()),

			// Used by ValidatorFactory
			pair("tks.validator.config", validatorConfigurationFile.toString())

			// needs additional configuration
		);
	}
}
