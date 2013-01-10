package uk.nhs.hcdn.common.tuples;

import uk.nhs.hcdn.common.comparison.ComparisonResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.comparison.ComparisonHelper;
import uk.nhs.hcdn.common.comparison.ComparisonResult;

import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isGreaterThan;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;

@SuppressWarnings("StandardVariableNames")
public final class ComparablePair<T extends Comparable<T>> extends Pair<T> implements Comparable<ComparablePair<T>>
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <T extends Comparable<T>> ComparablePair<T> comparablePair(@NotNull final T lowerBoundInclusive, @NotNull final T upperBoundInclusive)
	{
		return new ComparablePair<>(lowerBoundInclusive, upperBoundInclusive);
	}

	public ComparablePair(@NotNull final T a, @NotNull final T b)
	{
		super(a, b);
		if (ComparisonHelper.isGreaterThan(a.compareTo(b)))
		{
			throw new IllegalArgumentException("a can not be greater than b");
		}
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}

		final ComparablePair<?> that = (ComparablePair<?>) obj;

		if (!a.equals(that.a))
		{
			return false;
		}
		if (!b.equals(that.b))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = a.hashCode();
		result = (31 * result) + b.hashCode();
		return result;
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final ComparablePair<T> o)
	{
		@ComparisonResult final int aComparisonResult = a.compareTo(o.a);
		if (ComparisonHelper.isNotEqualTo(aComparisonResult))
		{
			return aComparisonResult;
		}
		return b.compareTo(o.b);
	}
}
