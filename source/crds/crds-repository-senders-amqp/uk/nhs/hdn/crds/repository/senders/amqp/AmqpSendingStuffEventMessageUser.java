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

import com.stormmq.amqp.client.sending.messagesToEnqueue.AbstractSingleChunkDataPayloadMessageToEnqueue;
import com.stormmq.amqp.client.sending.messagesToEnqueue.MessageToEnqueue;
import com.stormmq.amqp.types.primitive.other.AmqpUuid;
import com.stormmq.amqp.types.primitive.variable.AmqpString;
import com.stormmq.amqp.types.primitive.variable.Binary;
import com.stormmq.amqp.types.specification.definitions.messageId.MessageIdUuid;
import com.stormmq.amqp.types.specification.messaging.sections.ApplicationProperties;
import com.stormmq.amqp.types.specification.messaging.sections.Properties;
import com.stormmq.c.exceptions.CouldNotAllocateUnmanagedMemoryException;
import com.stormmq.nio.buffers.memory.MemoryBuffer;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;

import java.util.concurrent.BlockingQueue;

import static com.stormmq.amqp.client.sending.messagesToEnqueue.MessageTemplate.EmptyMessageTemplate;
import static com.stormmq.amqp.types.defaults.Definitions.NoAbsoluteExpiryTime;
import static com.stormmq.amqp.types.defaults.Definitions.NoGroupSequence;
import static com.stormmq.amqp.types.primitive.variable.AmqpString.newKnownGoodAmqpString;
import static uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread.AmqpConfiguration.*;

public final class AmqpSendingStuffEventMessageUser implements StuffEventMessageUser
{
	@NotNull public final BlockingQueue<MessageToEnqueue> messagesToEnqueue;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public AmqpSendingStuffEventMessageUser(@NotNull final BlockingQueue<MessageToEnqueue> messagesToEnqueue)
	{
		this.messagesToEnqueue = messagesToEnqueue;
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "FeatureEnvy"})
	@Override
	public void use(@NotNull final StuffEventMessage stuffEventMessage)
	{
		final StuffEvent stuffEvent = stuffEventMessage.stuffEvent();

		final ApplicationProperties applicationProperties = new ApplicationProperties(4);
		applicationProperties.put(ProviderIdApplicationPropertiesKey, amqpUuid(stuffEventMessage.providerIdentifier()));
		applicationProperties.put(RepositoryIdApplicationPropertiesKey, amqpUuid(stuffEventMessage.repositoryIdentifier()));
		applicationProperties.put(StuffIdApplicationPropertiesKey, amqpUuid(stuffEventMessage.stuffIdentifier()));
		applicationProperties.put(StuffEventKindApplicationPropertiesKey, stuffEventKind(stuffEvent)); // Wire Inefficient (could use the ordinal) but self-describing)

		messagesToEnqueue.offer(new AbstractSingleChunkDataPayloadMessageToEnqueue(EmptyMessageTemplate, new Properties(messageIdUuid(stuffEvent), userId(stuffEventMessage), null, subject(stuffEventMessage), null, null, StuffEventMessageContentType, null, NoAbsoluteExpiryTime, stuffEvent.timestamp, null, NoGroupSequence, null), applicationProperties)
		{
			@Override
			protected void storeChunk(@NotNull final MemoryBuffer<?> memoryBuffer)
			{
				memoryBuffer.putSignedBytes(stuffEventMessage.toWireFormatBytes());
			}
		});
	}

	@NotNull
	@SuppressWarnings("TypeMayBeWeakened")
	private static AmqpUuid amqpUuid(final Identifier identifier)
	{
		return new AmqpUuid(identifier.toUuid());
	}

	private static AmqpString subject(final StuffEventMessage stuffEventMessage)
	{
		return newKnownGoodAmqpString(stuffEventMessage.subject());
	}

	private static MessageIdUuid messageIdUuid(final StuffEvent stuffEvent)
	{
		return new MessageIdUuid(stuffEvent.stuffEventIdentifierUuid());
	}

	private static Binary userId(final StuffEventMessage stuffEventMessage)
	{
		final Binary userId;
		try
		{
			userId = new Binary(stuffEventMessage.userId());
		}
		catch (CouldNotAllocateUnmanagedMemoryException e)
		{
			throw new IllegalStateException(e);
		}
		return userId;
	}

}
