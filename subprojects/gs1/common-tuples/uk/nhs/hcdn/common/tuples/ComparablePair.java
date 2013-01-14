/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.tuples;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.comparison.ComparisonResult;

import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isGreaterThan;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;

@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
public final class ComparablePair<T extends Comparable<T>> extends Pair<T, T> implements Comparable<ComparablePair<T>>
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
		if (isGreaterThan(a.compareTo(b)))
		{
			throw new IllegalArgumentException("a can not be greater than b");
		}
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final ComparablePair<T> o)
	{
		@ComparisonResult final int aComparisonResult = a.compareTo(o.a);
		if (isNotEqualTo(aComparisonResult))
		{
			return aComparisonResult;
		}
		return b.compareTo(o.b);
	}
}
