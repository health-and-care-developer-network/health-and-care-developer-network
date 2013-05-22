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

import com.stormmq.amqp.client.receiving.AbstractFooterValidatingMessageReceiver;
import com.stormmq.amqp.client.receiving.ReceivedMessage;
import com.stormmq.amqp.client.sending.messagesToEnqueue.footerGenerators.footerKeyValuePairGenerators.HashFooterKeyValuePairGenerator;
import com.stormmq.amqp.client.sending.messagesToEnqueue.footerGenerators.footerKeyValuePairGenerators.HashMessageAuthenticationCodeFooterKeyValuePairGenerator;
import com.stormmq.amqp.nyx.link.LinkRecord;
import com.stormmq.amqp.nyx.transferringMessages.transferringMessageRecords.TransferringMessageRecord;
import com.stormmq.amqp.types.exceptions.terminateBecauseOfErrorExceptions.detachLinkBecauseOfErrorExceptions.LinkUnsettledMessageSpaceExhaustedDetachLinkBecauseOfErrorException;
import com.stormmq.amqp.types.exceptions.terminateBecauseOfErrorExceptions.detachLinkBecauseOfErrorExceptions.RequiredTerminalOutcomeNotSupportedByLinkDetachLinkBecauseOfErrorException;
import com.stormmq.amqp.types.primitive.SimplePrimitive;
import com.stormmq.amqp.types.primitive.other.AmqpUuid;
import com.stormmq.amqp.types.primitive.variable.AmqpString;
import com.stormmq.amqp.types.primitive.variable.Symbol;
import com.stormmq.amqp.types.specification.definitions.messageId.MessageId;
import com.stormmq.amqp.types.specification.definitions.messageId.MessageIdUuid;
import com.stormmq.amqp.types.specification.messaging.sections.ApplicationProperties;
import com.stormmq.amqp.types.specification.messaging.sections.Footer;
import com.stormmq.amqp.types.specification.messaging.sections.Header;
import com.stormmq.amqp.types.specification.messaging.sections.Properties;
import com.stormmq.amqp.types.specification.messaging.transferState.Accepted;
import com.stormmq.amqp.types.specification.messaging.transferState.Outcome;
import com.stormmq.amqp.types.specification.messaging.transferState.Rejected;
import com.stormmq.amqp.types.specification.transport.errors.Error;
import com.stormmq.nio.memory.struct.Uuid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffEventKind;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.number.NhsNumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.stormmq.amqp.types.primitive.variable.AmqpString.newKnownGoodAmqpString;
import static com.stormmq.amqp.types.specification.transport.errors.AmqpError.InvalidField;
import static com.stormmq.logging.LastResortAuditLog.critical;
import static java.util.Collections.emptySet;
import static uk.nhs.hdn.crds.registry.domain.StuffEventKind.*;
import static uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread.AmqpConfiguration.*;

public final class AmqpStuffEventMessageReceiver extends AbstractFooterValidatingMessageReceiver
{
	@NotNull private static final Set<HashMessageAuthenticationCodeFooterKeyValuePairGenerator> NoMandatoryHmacs = emptySet();
	@NotNull private static final HashFooterKeyValuePairGenerator[] NoHashesWhichMustBePresentAndCorrect = {};
	@NotNull private static final Outcome<?> NoPropertiesError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("No properties for StuffEvent message"), null));
	@NotNull private static final Outcome<?> NoApplicationPropertiesError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("No application-properties for StuffEvent message"), null));
	@NotNull private static final Outcome<?> NoMessageIdError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("No properties message-id for StuffEvent message"), null));
	@NotNull private static final Outcome<?> MessageIdNotUuidError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("Properties message-id is not an UUID for StuffEvent message"), null));
	@NotNull private static final Outcome<?> NoSubjectError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("No properties subject for StuffEvent message"), null));
	@NotNull private static final Outcome<?> InvalidNhsNumberError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("Properties subject is an invalid NHS Number for StuffEvent message"), null));
	@NotNull private static final Outcome<?> NoTimestampError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("No properties creation_time for StuffEvent message"), null));
	@NotNull private static final Outcome<?> UnrecognisedStuffEventKindError = new Rejected(new Error(InvalidField, newKnownGoodAmqpString("Unrecognised application-properties StuffEventKind"), null));
	@NotNull private static final IncorrectApplicationPropertiesValueException MissingProviderIdentifier = new IncorrectApplicationPropertiesValueException(ProviderIdApplicationPropertiesKey, "key %1$s is required");
	@NotNull private static final IncorrectApplicationPropertiesValueException ProviderIdentifierNotAnUuid = new IncorrectApplicationPropertiesValueException(ProviderIdApplicationPropertiesKey, "key %1$s is not an UUID");
	@NotNull private static final IncorrectApplicationPropertiesValueException MissingRepositoryIdentifier = new IncorrectApplicationPropertiesValueException(RepositoryIdApplicationPropertiesKey, "key %1$s is required");
	@NotNull private static final IncorrectApplicationPropertiesValueException RepositoryIdentifierNotAnUuid = new IncorrectApplicationPropertiesValueException(RepositoryIdApplicationPropertiesKey, "key %1$s is not an UUID");
	@NotNull private static final IncorrectApplicationPropertiesValueException MissingStuffIdentifier = new IncorrectApplicationPropertiesValueException(StuffIdApplicationPropertiesKey, "key %1$s is required");
	@NotNull private static final IncorrectApplicationPropertiesValueException StuffIdentifierNotAnUuid = new IncorrectApplicationPropertiesValueException(StuffIdApplicationPropertiesKey, "key %1$s is not an UUID");
	@NotNull private static final IncorrectApplicationPropertiesValueException MissingStuffEventKind = new IncorrectApplicationPropertiesValueException(StuffEventKindApplicationPropertiesKey, "key %1$s is required");
	@NotNull private static final IncorrectApplicationPropertiesValueException StuffEventKindNotASymbol = new IncorrectApplicationPropertiesValueException(StuffEventKindApplicationPropertiesKey, "key %1$s is not a Symbol");
	@SuppressWarnings({"serial", "ClassExtendsConcreteCollection"}) @NotNull private static final Map<Symbol, StuffEventKind> StuffEventKindMapping = new HashMap<Symbol, StuffEventKind>(3)
	{{
		put(StuffEventKindCreated, Created);
		put(StuffEventKindUpdated, Updated);
		put(StuffEventKindRemoved, Removed);
	}};

	@NotNull private final PatientRecordStore patientRecordStore;

	public AmqpStuffEventMessageReceiver(@NotNull final LinkRecord linkRecord, @NotNull final PatientRecordStore patientRecordStore)
	{
		super(linkRecord, NoMandatoryHmacs, NoHashesWhichMustBePresentAndCorrect);
		this.patientRecordStore = patientRecordStore;
	}

	@SuppressWarnings({"OverlyLongMethod", "MethodWithMultipleReturnPoints"})
	@NotNull
	@Override
	protected Outcome<?> useFooterVerifiedMessage(@NotNull final ReceivedMessage receivedMessage, @NotNull final Header header, @Nullable final Properties properties, @Nullable final ApplicationProperties applicationProperties, @Nullable final Footer footer)
	{
		if (properties == null)
		{
			return NoPropertiesError;
		}

		if (applicationProperties == null)
		{
			return NoApplicationPropertiesError;
		}

		@Nullable final MessageId messageId = properties.messageId();
		if (messageId == null)
		{
			return NoMessageIdError;
		}
		if (!(messageId instanceof MessageIdUuid))
		{
			return MessageIdNotUuidError;
		}
		final StuffEventIdentifier stuffEventIdentifier = new StuffEventIdentifier(((Uuid) messageId).asJavaUUID());

		@Nullable final AmqpString subject = properties.subject();
		if (subject == null)
		{
			return NoSubjectError;
		}
		final NhsNumber patientIdentifier;
		try
		{
			patientIdentifier = NhsNumber.valueOf(subject.asString());
		}
		catch (RuntimeException ignored)
		{
			return InvalidNhsNumberError;
		}

		@MillisecondsSince1970 final long stuffEventTimestamp = properties.creationTime();
		if (stuffEventTimestamp == MillisecondsSince1970.NullMillisecondsSince1970)
		{
			return NoTimestampError;
		}

		final ProviderIdentifier providerIdentifier;
		final RepositoryIdentifier repositoryIdentifier;
		final StuffIdentifier stuffIdentifier;
		final Symbol stuffEventKindSymbol;
		try
		{
			providerIdentifier = new ProviderIdentifier(getUuid(applicationProperties, ProviderIdApplicationPropertiesKey, MissingProviderIdentifier, ProviderIdentifierNotAnUuid).asJavaUUID());
			repositoryIdentifier = new RepositoryIdentifier(getUuid(applicationProperties, RepositoryIdApplicationPropertiesKey, MissingRepositoryIdentifier, RepositoryIdentifierNotAnUuid).asJavaUUID());
			stuffIdentifier = new StuffIdentifier(getUuid(applicationProperties, StuffIdApplicationPropertiesKey, MissingStuffIdentifier, StuffIdentifierNotAnUuid).asJavaUUID());
			stuffEventKindSymbol = getStuffEventKind(applicationProperties, StuffEventKindApplicationPropertiesKey, MissingStuffEventKind, StuffEventKindNotASymbol);
		}
		catch (IncorrectApplicationPropertiesValueException e)
		{
			return e.outcome;
		}

		@Nullable final StuffEventKind stuffEventKind = StuffEventKindMapping.get(stuffEventKindSymbol);
		if (stuffEventKind == null)
		{
			return UnrecognisedStuffEventKindError;
		}

		patientRecordStore.addEvent(new StuffEventMessage(patientIdentifier, providerIdentifier, repositoryIdentifier, stuffIdentifier, new StuffEvent(stuffEventIdentifier, stuffEventTimestamp, stuffEventKind)));
		return Accepted.Accepted;
	}

	@NotNull
	private static AmqpUuid getUuid(final ApplicationProperties applicationProperties, final AmqpString key, final IncorrectApplicationPropertiesValueException missingValue, final IncorrectApplicationPropertiesValueException notAnUuid) throws IncorrectApplicationPropertiesValueException
	{
		@Nullable final SimplePrimitive simplePrimitive = applicationProperties.get(key);
		if (simplePrimitive == null)
		{
			throw missingValue;
		}
		if (simplePrimitive instanceof AmqpUuid)
		{
			return (AmqpUuid) simplePrimitive;
		}
		throw notAnUuid;
	}

	@NotNull
	private static Symbol getStuffEventKind(final ApplicationProperties applicationProperties, final AmqpString key, final IncorrectApplicationPropertiesValueException missingValue, final IncorrectApplicationPropertiesValueException notAnUuid) throws IncorrectApplicationPropertiesValueException
	{
		@Nullable final SimplePrimitive simplePrimitive = applicationProperties.get(key);
		if (simplePrimitive == null)
		{
			throw missingValue;
		}
		if (simplePrimitive instanceof Symbol)
		{
			return (Symbol) simplePrimitive;
		}
		throw notAnUuid;
	}

	@Override
	public void terminalOutcomeNotPermitted(@NotNull final TransferringMessageRecord transferringMessageRecord, @NotNull final RequiredTerminalOutcomeNotSupportedByLinkDetachLinkBecauseOfErrorException e)
	{
		critical(e);
	}

	@Override
	public void outOfMemory(@NotNull final TransferringMessageRecord transferringMessageRecord, @NotNull final LinkUnsettledMessageSpaceExhaustedDetachLinkBecauseOfErrorException e)
	{
		critical(e);
	}

}
