package uk.nhs.hdn.dts.rats.request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseException;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.Serialisable;
import uk.nhs.hdn.common.serialisers.Serialiser;
import uk.nhs.hdn.common.serialisers.xml.XmlSerialiser;
import uk.nhs.hdn.dts.rats.Message;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.tuples.Pair.pair;
import static uk.nhs.hdn.common.xml.XmlNamespaceUri.XmlSchemaInstanceNamespace;
import static uk.nhs.hdn.dts.domain.Version.VersionMajor1Minor0;

public final class Messages extends AbstractToString implements Serialisable
{
	@NotNull
	private final Message[] messages;

	public Messages(@NotNull final Message... messages)
	{
		this.messages = copyOf(messages);
	}

	@NotNull
	public static XmlSerialiser messagesXmlSerialiser()
	{
		return new XmlSerialiser
		(
			true,
			"DTSRequest",
			of
			(
				pair(XmlSchemaInstanceNamespace, "xsi")
			),
			of
			(
				pair("xsi:noNamespaceSchemaLocation", "D:\\NHSRegSys\\Backend\\www\\Schema\\dts request.xsd"),
				pair("Version", VersionMajor1Minor0.actualName())
			)
		);
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			for (final Message message : messages)
			{
				message.serialiseMap(serialiser);
			}
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public List<Message> messages()
	{
		return asList(messages);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Messages messages1 = (Messages) obj;

		if (!Arrays.equals(messages, messages1.messages))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(messages);
	}
}
