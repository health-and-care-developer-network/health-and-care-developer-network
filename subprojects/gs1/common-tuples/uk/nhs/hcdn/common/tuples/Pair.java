package uk.nhs.hcdn.common.tuples;

import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("StandardVariableNames")
public class Pair<T> extends AbstractToString
{
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final T a;

	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final T b;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <T> Pair<T> pair(@NotNull final T a, @NotNull final T b)
	{
		return new Pair<>(a, b);
	}

	public Pair(@NotNull final T a, @NotNull final T b)
	{
		this.a = a;
		this.b = b;
	}
}
