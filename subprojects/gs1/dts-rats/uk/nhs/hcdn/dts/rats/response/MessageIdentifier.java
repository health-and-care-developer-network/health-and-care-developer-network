package uk.nhs.hcdn.dts.rats.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.dts.domain.identifiers.AbstractIdentifier;

import static java.lang.Integer.MAX_VALUE;

public final class MessageIdentifier extends AbstractIdentifier
{
	public MessageIdentifier(@NonNls @NotNull final String value)
	{
		super(value, MAX_VALUE);
	}
}
