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

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;

import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class HazelcastSendingStuffEventMessageUser implements StuffEventMessageUser
{
	public static final long TenMilliseconds = 10L;
	@NotNull private final BlockingQueue<StuffEventMessage> stuffEventMessages;

	public HazelcastSendingStuffEventMessageUser(@NotNull final HazelcastConfiguration hazelcastConfiguration)
	{
		stuffEventMessages = hazelcastConfiguration.rootQueue();
	}

	@Override
	public void use(@NotNull final StuffEventMessage stuffEventMessage)
	{
		boolean success;
		do
		{
			try
			{
				success = stuffEventMessages.offer(stuffEventMessage, TenMilliseconds, MILLISECONDS);
			}
			catch (InterruptedException ignored)
			{
				currentThread().interrupt();
				success = false;
			}
		} while(success);
	}
}
