/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.common.tuples;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.comparison.ComparisonResult;

import static uk.nhs.hdn.common.comparison.ComparisonHelper.isGreaterThan;
import static uk.nhs.hdn.common.comparison.ComparisonHelper.isNotEqualTo;

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
