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

import com.stormmq.amqp.types.primitive.variable.AmqpString;
import com.stormmq.amqp.types.specification.messaging.transferState.Outcome;
import com.stormmq.amqp.types.specification.messaging.transferState.Rejected;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.AbstractRethrowableException;

import static com.stormmq.amqp.types.primitive.variable.AmqpString.newKnownGoodAmqpString;
import static com.stormmq.amqp.types.specification.transport.errors.AmqpError.InvalidField;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectApplicationPropertiesValueException extends AbstractRethrowableException
{
	@SuppressWarnings({"PublicField", "NonSerializableFieldInSerializableClass"}) @NotNull public final Outcome<?> outcome;

	public IncorrectApplicationPropertiesValueException(@NotNull final AmqpString key, @NonNls @NotNull final String descriptionTemplate)
	{
		super("Incorrect application-properties value");
		outcome = new Rejected(new com.stormmq.amqp.types.specification.transport.errors.Error(InvalidField, newKnownGoodAmqpString(format(ENGLISH, descriptionTemplate, key.toString())), null));
	}
}
