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

package uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread;

import com.stormmq.amqp.types.specification.terminus.Source;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.registry.domain.Configuration;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.stormmq.amqp.client.convenience.MessageReceiverRunnable.startMessageReceivingOnlyAmqpConnectionThread;
import static com.stormmq.amqp.client.utilities.PgpassFileParser.findBase64UrlEncodedPasswordFromPgPassFile;
import static com.stormmq.amqp.encryption.EncryptionLevel.AttemptToNegotiateAmqpTlsSecurityLayer;
import static com.stormmq.amqp.types.defaults.Definitions.LinkExpiryZeroTimeout;
import static com.stormmq.amqp.types.domain.OutcomeSymbol.AllNonTransactionalOutcomes;
import static com.stormmq.amqp.types.domain.capabilities.SourceCapability.EmptySourceCapabilities;
import static com.stormmq.amqp.types.specification.definitions.address.AddressString.newKnownGoodAddressString;
import static com.stormmq.amqp.types.specification.filtering.FilterSet.EmptyFilterSet;
import static com.stormmq.amqp.types.specification.messaging.transferState.Released.Released;
import static com.stormmq.amqp.types.specification.terminus.supporting.NodeProperties.EmptyNodeProperties;
import static com.stormmq.amqp.types.specification.terminus.supporting.StdDistMode.move;
import static com.stormmq.amqp.types.specification.terminus.supporting.TerminusDurability.unsettled_state;
import static com.stormmq.amqp.types.specification.terminus.supporting.TerminusExpiryPolicy.never;
import static uk.nhs.hdn.crds.registry.domain.Configuration.HostName;
import static uk.nhs.hdn.crds.registry.domain.Configuration.UserName;
import static uk.nhs.hdn.crds.registry.domain.Configuration.VirtualHostName;

public final class AmqpStartReceivingMessagesThread implements StartReceivingMessagesThread
{
	@NotNull private static final Source QueueSettings = new Source(newKnownGoodAddressString(Configuration.QueueName), unsettled_state, never, LinkExpiryZeroTimeout, false, EmptyNodeProperties, move, EmptyFilterSet, Released, AllNonTransactionalOutcomes, EmptySourceCapabilities);

	@NotNull private final File dataPath;
	private final int instanceId;
	@NotNull private final String base64urlEncodedPassword;

	public AmqpStartReceivingMessagesThread(@NotNull final File dataPath, final int instanceId, @NotNull final File pgpassFile) throws IOException
	{
		@Nullable final String base64urlEncodedPassword = findBase64UrlEncodedPasswordFromPgPassFile(pgpassFile, HostName, VirtualHostName, UserName);
		if (base64urlEncodedPassword == null)
		{
			throw new IllegalArgumentException("No password could be determined");
		}
		this.dataPath = dataPath;
		this.instanceId = instanceId;
		this.base64urlEncodedPassword = base64urlEncodedPassword;
	}

	@Override
	@NotNull
	public AtomicBoolean startReceivingMessagesThread(@NotNull final PatientRecordStore patientRecordStore)
	{
		return startMessageReceivingOnlyAmqpConnectionThread(AttemptToNegotiateAmqpTlsSecurityLayer, HostName, VirtualHostName, ourContainerId(instanceId), UserName, base64urlEncodedPassword, new AmqpStuffEventMessageReceiverFactory(patientRecordStore), QueueSettings, dataPath, false);
	}

	@NonNls
	private static String ourContainerId(final int instanceId)
	{
		return "registry-" + Integer.toString(instanceId);
	}
}
