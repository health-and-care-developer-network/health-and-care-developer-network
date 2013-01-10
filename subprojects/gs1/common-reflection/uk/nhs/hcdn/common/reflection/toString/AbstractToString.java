package uk.nhs.hcdn.common.reflection.toString;

public abstract class AbstractToString
{
	protected AbstractToString()
	{
	}

	@Override
	public final String toString()
	{
		return ToStringHelper.toString(this);
	}
}
