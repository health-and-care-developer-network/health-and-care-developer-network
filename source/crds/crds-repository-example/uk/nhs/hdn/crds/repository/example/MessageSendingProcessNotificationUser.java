package uk.nhs.hdn.crds.repository.example;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.sql.postgresql.ProcessNotificationUser;

import java.io.IOException;
import java.io.StringReader;

import static uk.nhs.hdn.common.MillisecondsSince1970.NullMillisecondsSince1970;

public final class MessageSendingProcessNotificationUser extends AbstractToString implements ProcessNotificationUser
{
	@NotNull private final Parser parser;

	public MessageSendingProcessNotificationUser(@NotNull final Parser parser)
	{
		this.parser = parser;
	}

	@Override
	public void processNotification(@NotNull final String channel, @NotNull final String message)
	{
		try
		{
			parser.parse(new StringReader(message), NullMillisecondsSince1970);
		}
		catch (IOException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (CouldNotParseException e)
		{
			throw new IllegalStateException("Check the format of notifications from Postgresql", e);
		}
	}
}
