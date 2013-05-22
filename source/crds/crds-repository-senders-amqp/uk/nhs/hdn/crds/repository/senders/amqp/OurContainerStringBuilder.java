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

package uk.nhs.hdn.crds.repository.senders.amqp;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;

public final class OurContainerStringBuilder
{
	private OurContainerStringBuilder()
	{
	}

	// Containers must be long-lived and unique
	@NotNull
	@NonNls
	public static String ourContainerString(@SuppressWarnings("TypeMayBeWeakened") @NotNull final ProviderIdentifier providerIdentifier, @SuppressWarnings("TypeMayBeWeakened") @NotNull final RepositoryIdentifier repositoryIdentifier, final int instanceId)
	{
		return providerIdentifier.toUuidString() + '.' + repositoryIdentifier.toUuidString() + '.' + Integer.toString(instanceId);
	}
}
