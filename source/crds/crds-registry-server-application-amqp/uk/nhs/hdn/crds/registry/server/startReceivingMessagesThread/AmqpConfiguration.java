package uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread;

import com.stormmq.amqp.types.primitive.variable.AmqpString;
import com.stormmq.amqp.types.primitive.variable.Symbol;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.ImpossibleEnumeratedStateException;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;

import static com.stormmq.amqp.types.primitive.variable.AmqpString.newKnownGoodAmqpString;
import static com.stormmq.amqp.types.primitive.variable.Symbol.newKnownGoodSymbol;
import static uk.nhs.hdn.crds.registry.domain.StuffEventKind.*;

public final class AmqpConfiguration
{
	private AmqpConfiguration()
	{
	}

	@NotNull public static final Symbol StuffEventMessageContentType = newKnownGoodSymbol("text/csv;charset=utf-8;header=absent");
	@NotNull public static final AmqpString ProviderIdApplicationPropertiesKey = newKnownGoodAmqpString("ProviderId");
	@NotNull public static final AmqpString RepositoryIdApplicationPropertiesKey = newKnownGoodAmqpString("RepositoryId");
	@NotNull public static final AmqpString StuffIdApplicationPropertiesKey = newKnownGoodAmqpString("StuffId");
	@NotNull public static final AmqpString StuffEventKindApplicationPropertiesKey = newKnownGoodAmqpString("StuffEventKind");
	@NotNull public static final Symbol StuffEventKindCreated = symbol(Created);
	@NotNull public static final Symbol StuffEventKindUpdated = symbol(Updated);
	@NotNull public static final Symbol StuffEventKindRemoved = symbol(Removed);

	@NotNull
	public static Symbol symbol(@NotNull final Enum<?> enumValue)
	{
		return newKnownGoodSymbol(enumValue.name());
	}

	@SuppressWarnings("TypeMayBeWeakened")
	@NotNull
	public static Symbol stuffEventKind(@NotNull final StuffEvent stuffEvent)
	{
		switch (stuffEvent.stuffEventKind)
		{
			case Created:
				return StuffEventKindCreated;

			case Updated:
				return StuffEventKindUpdated;

			case Removed:
				return StuffEventKindRemoved;

			default:
				throw new ImpossibleEnumeratedStateException();
		}
	}
}
