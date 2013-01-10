package uk.nhs.hcdn.common.exceptions;

public final class ImpossibleEnumeratedStateException extends IllegalStateException
{
	public ImpossibleEnumeratedStateException()
	{
		super("Impossible enumerated state");
	}
}
