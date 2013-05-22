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

package uk.nhs.hdn.crds.repository.senders.hazelcast;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.repository.example.AbstractRepositoryExampleApplication;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HazelcastRepositoryExampleApplication extends AbstractRepositoryExampleApplication<HazelcastSendingStuffEventMessageUser>
{
	@NotNull private final File persistedMessagesPath;
	@NotNull private final String user;
	@NotNull private final String base64urlEncodedPassword;
	private final int instanceId;
	@NotNull private final String hostName;
	@NotNull private final String virtualHostName;
	@NotNull private final String queueName;

	public HazelcastRepositoryExampleApplication(@NotNull final File persistedMessagesPath, @NonNls @NotNull final String user, @NonNls @NotNull final String base64urlEncodedPassword, final int instanceId, @NotNull @NonNls final String hostName, @NotNull @NonNls final String virtualHostName, @NotNull @NonNls final String queueName, @NotNull final HazelcastConfiguration hazelcastConfiguration)
	{
		super(new HazelcastSendingStuffEventMessageUser(hazelcastConfiguration));
		this.persistedMessagesPath = persistedMessagesPath;
		this.user = user;
		this.base64urlEncodedPassword = base64urlEncodedPassword;
		this.instanceId = instanceId;
		this.hostName = hostName;
		this.virtualHostName = virtualHostName;
		this.queueName = queueName;
	}

	@Override
	@NotNull
	protected AtomicBoolean startMessageSender()
	{
		return new AtomicBoolean(false);
	}
}
