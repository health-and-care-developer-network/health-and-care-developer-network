package uk.nhs.hcdn.dts.rats.response.details;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.unknown.AbstractIsUnknown;

public final class UnknownDetails extends AbstractIsUnknown implements Details
{
	@NotNull
	public static final Details UnknownDetailsInstance = new UnknownDetails();

	private UnknownDetails()
	{
		super(true);
	}
}
