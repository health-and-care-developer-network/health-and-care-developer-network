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

import com.stormmq.amqp.interaction.messageReceiver.MessageReceiverFactory;
import com.stormmq.amqp.nyx.link.LinkRecord;
import com.stormmq.amqp.nyx.link.messageReceivers.MessageReceiver;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;

public final class AmqpStuffEventMessageReceiverFactory implements MessageReceiverFactory
{
	private final PatientRecordStore patientRecordStore;

	public AmqpStuffEventMessageReceiverFactory(@NotNull final PatientRecordStore patientRecordStore)
	{
		this.patientRecordStore = patientRecordStore;
	}

	@NotNull
	@Override
	public MessageReceiver messageReceiver(@NotNull final LinkRecord linkRecord)
	{
		return new AmqpStuffEventMessageReceiver(linkRecord, patientRecordStore);
	}
}
