package uk.nhs.hdn.dts.rats.response.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.dts.rats.response.Response;

public final class ResponseArrayCreator extends AbstractArrayCreator<Response>
{
	@NotNull
	public static final ArrayCreator<Response> ResponseArrayCreatorInstance = new ResponseArrayCreator();

	ResponseArrayCreator()
	{
		super(Response.class, Response[].class);
	}

	@NotNull
	@Override
	public Response[] newInstance1(final int size)
	{
		return new Response[size];
	}

	@NotNull
	@Override
	public Response[][] newInstance2(final int size)
	{
		return new Response[size][];
	}
}
