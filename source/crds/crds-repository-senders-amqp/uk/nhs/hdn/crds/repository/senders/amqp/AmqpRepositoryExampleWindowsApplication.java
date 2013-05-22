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

import com.stormmq.amqp.client.sending.messagesToEnqueue.MessageToEnqueue;
import com.stormmq.amqp.types.specification.terminus.Target;
import com.stormmq.amqp.types.specification.terminus.lifetimePolicy.DeleteOnClose;
import com.stormmq.amqp.types.specification.terminus.supporting.NodeProperties;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.repository.example.windows.AbstractRepositoryExampleWindowsApplication;

import java.io.File;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.stormmq.amqp.client.convenience.MessageSenderRunnable.startMessageSendingOnlyAmqpConnectionThread;
import static com.stormmq.amqp.encryption.EncryptionLevel.AttemptToNegotiateAmqpTlsSecurityLayer;
import static com.stormmq.amqp.types.defaults.Definitions.LinkExpiryZeroTimeout;
import static com.stormmq.amqp.types.domain.capabilities.TargetCapability.EmptyTargetCapabilities;
import static com.stormmq.amqp.types.specification.definitions.address.AddressString.newKnownGoodAddressString;
import static com.stormmq.amqp.types.specification.terminus.supporting.TerminusDurability.unsettled_state;
import static com.stormmq.amqp.types.specification.terminus.supporting.TerminusExpiryPolicy.session_end;
import static uk.nhs.hdn.crds.repository.example.SchemaCreation.OurProviderIdentifier;
import static uk.nhs.hdn.crds.repository.example.SchemaCreation.OurRepositoryIdentifier;
import static uk.nhs.hdn.crds.repository.senders.amqp.OurContainerStringBuilder.ourContainerString;

public final class AmqpRepositoryExampleWindowsApplication extends AbstractRepositoryExampleWindowsApplication<AmqpSendingStuffEventMessageUser>
{
	@NotNull private final File persistedMessagesPath;
	@NotNull private final String user;
	@NotNull private final String base64urlEncodedPassword;
	private final int instanceId;
	@NotNull private final String hostName;
	@NotNull private final String virtualHostName;
	@NotNull private final String queueName;

	public AmqpRepositoryExampleWindowsApplication(@NotNull final File persistedMessagesPath, @NonNls @NotNull final String user, @NonNls @NotNull final String base64urlEncodedPassword, final int instanceId, @NotNull @NonNls final String hostName, @NotNull @NonNls final String virtualHostName, @NotNull @NonNls final String queueName)
	{
		super(new AmqpSendingStuffEventMessageUser(new LinkedBlockingDeque<MessageToEnqueue>()));
		this.persistedMessagesPath = persistedMessagesPath;
		this.user = user;
		this.base64urlEncodedPassword = base64urlEncodedPassword;
		this.instanceId = instanceId;
		this.hostName = hostName;
		this.virtualHostName = virtualHostName;
		this.queueName = queueName;
	}

	@NotNull
	@Override
	protected AtomicBoolean startMessageSender()
	{
		final String ourContainerString = ourContainerString(OurProviderIdentifier, OurRepositoryIdentifier, instanceId);
		final Target target = new Target(newKnownGoodAddressString(queueName), unsettled_state, session_end, LinkExpiryZeroTimeout, true, new NodeProperties(DeleteOnClose.DeleteOnClose), EmptyTargetCapabilities);
		final BlockingQueue<MessageToEnqueue> messagesToEnqueue = sendingStuffEventMessageUser.messagesToEnqueue;
		return startMessageSendingOnlyAmqpConnectionThread(AttemptToNegotiateAmqpTlsSecurityLayer, hostName, virtualHostName, ourContainerString, user, base64urlEncodedPassword, (BlockingDeque<MessageToEnqueue>) messagesToEnqueue, target, persistedMessagesPath, true);
	}
}
